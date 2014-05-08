package org.tju.so.crawler.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.rule.Rule;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private static final Integer IDLE_WAIT = 1000;

    private static final Integer BUSY_WAIT = 64;

    @Autowired
    private Storage storage;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private Scheduler scheduler;

    @Resource
    private int workerThreadNumber;

    private volatile boolean isRunning;

    private List<WorkerThread> threads;

    @PostConstruct
    public void init() {
        threads = new ArrayList<WorkerThread>();
        for (int i = 0; i < workerThreadNumber; i++) {
            threads.add(new WorkerThread());
        }
        LOG.info(threads.size() + " worker thread(s) created.");
    }

    public void reload() {
        init();
        taskExecutor.reload();
    }

    private void runTask(Task task) throws Exception {
        String url = task.getUrl();
        Rule rule = scheduler.getRule(url);
        taskExecutor.executeTask(rule, task);
    }

    public boolean doWork() throws Exception {
        Task task = null;
        task = storage.popTask(TaskPriority.HIGHER);
        if (task == null)
            task = storage.popTask(TaskPriority.NORMAL);
        if (task == null)
            task = storage.popTask(TaskPriority.LOWER);

        if (task != null) {
            LOG.info("Task poped: " + task.toString());
            try {
                runTask(task);
            } catch (Exception e) {
                LOG.warn("Error occured during task execution", e);
            }
            return true;
        } else {
            return false;
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (isRunning) {
                int waitMillis = IDLE_WAIT;
                try {
                    if (doWork())
                        waitMillis = BUSY_WAIT;
                } catch (Exception e) {
                    LOG.error("Storage operation failed", e);
                }
                try {
                    Thread.sleep(waitMillis);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public void stop() {
        LOG.info("Stopping worker thread(s)...");
        isRunning = false;
        for (WorkerThread thread: threads) {
            thread.interrupt();
        }
    }

    public void run() {
        LOG.info("Starting worker thread(s)...");
        isRunning = true;
        for (WorkerThread thread: threads) {
            thread.start();
        }
    }

}
