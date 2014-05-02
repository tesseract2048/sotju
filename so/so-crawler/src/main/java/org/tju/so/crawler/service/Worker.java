package org.tju.so.crawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.data.Task;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    @Autowired
    private Storage storage;

    @Autowired
    private TaskExecutor taskExecutor;

    private volatile boolean isRunning;

    public void doWork() throws Exception {
        Task task = null;
        task = storage.popTask(TaskPriority.HIGHER);
        if (task == null)
            task = storage.popTask(TaskPriority.NORMAL);
        if (task == null)
            task = storage.popTask(TaskPriority.LOWER);

        if (task != null) {
            LOG.info("Task poped: " + task.toString());
            try {
                taskExecutor.executeTask(task);
            } catch (Exception e) {
                LOG.warn("Error occured during task execution", e);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void run() throws Exception {
        isRunning = true;
        while (isRunning) {
            doWork();
            Thread.sleep(1000);
        }
    }

}
