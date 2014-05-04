package org.tju.so.crawler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.holder.RuleHolder;
import org.tju.so.model.crawler.rule.Rule;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    @Autowired
    private Storage storage;

    @Autowired
    private RuleHolder ruleHolder;

    @Autowired
    private TaskExecutor taskExecutor;

    @Resource
    private int workerThreadNumber;

    private volatile boolean isRunning;

    private Map<String, Rule> rules;

    private List<WorkerThread> threads;

    @PostConstruct
    public void init() {
        rules = new HashMap<String, Rule>();
        for (Rule rule: ruleHolder.getAll()) {
            String pattern = rule.getUrlPattern();
            rules.put(pattern, rule);
            LOG.info("Rule loaded for pattern " + pattern);
        }
        threads = new ArrayList<WorkerThread>();
        for (int i = 0; i < workerThreadNumber; i++) {
            threads.add(new WorkerThread());
        }
        LOG.info(threads.size() + " worker thread(s) created.");
    }

    private Rule getRule(String url) {
        for (Map.Entry<String, Rule> entry: rules.entrySet()) {
            if (url.matches(entry.getKey()))
                return entry.getValue();
        }
        return null;
    }

    public void reload() {
        ruleHolder.flush();
        init();
    }

    private boolean checkRefresh(String url, int refreshRate) throws Exception {
        long lastRefreshTime = storage.getRefresh(url);
        long currentTime = storage.currentTime();
        if (lastRefreshTime > 0 && refreshRate == Rule.NEVER_REFRESH)
            return false;
        if (currentTime - lastRefreshTime < refreshRate)
            return false;
        storage.putRefresh(url, currentTime);
        return true;
    }

    private void runTask(Task task) throws Exception {
        String url = task.getUrl();
        Rule rule = getRule(url);
        if (rule == null) {
            storage.removeContext(task.getContextId());
            throw new Exception("No rule found for: " + url);
        }
        if (!checkRefresh(url, rule.getRefreshRate())) {
            storage.removeContext(task.getContextId());
            LOG.info("Refresh limit exceeded: " + url);
            return;
        }
        taskExecutor.executeTask(rule, task);
    }

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
                runTask(task);
            } catch (Exception e) {
                LOG.warn("Error occured during task execution", e);
            }
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (isRunning) {
                try {
                    doWork();
                } catch (Exception e) {
                    LOG.error("Operation failure with storage", e);
                }
                try {
                    Thread.sleep(1000);
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
