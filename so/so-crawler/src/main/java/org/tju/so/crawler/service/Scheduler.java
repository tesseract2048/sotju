package org.tju.so.crawler.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.holder.RuleHolder;
import org.tju.so.model.crawler.holder.SiteConfigHolder;
import org.tju.so.model.crawler.rule.Rule;
import org.tju.so.model.crawler.rule.Seed;
import org.tju.so.model.crawler.rule.SiteConfig;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private SiteConfigHolder siteConfigHolder;

    @Autowired
    private Storage storage;

    @Autowired
    private RuleHolder ruleHolder;

    private List<SiteConfig> siteConfigs;

    private volatile boolean isRunning;

    private Map<String, Rule> rules;

    public boolean scheduleNewTask(Task task) throws Exception {
        String url = task.getUrl();
        Rule rule = getRule(url);
        if (rule == null) {
            LOG.error("No rule found for: " + url);
            return false;
        }
        if (!checkRefresh(url, rule.getRefreshRate())) {
            LOG.info("Refresh limit exceeded: " + url);
            return false;
        }
        LOG.info("Scheduled: " + task.getUrl());
        storage.pushTask(task);
        return true;
    }

    public Rule getRule(String url) {
        for (Map.Entry<String, Rule> entry: rules.entrySet()) {
            if (url.matches(entry.getKey()))
                return entry.getValue();
        }
        return null;
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

    @PostConstruct
    public void init() {
        LOG.info("Initializing crawler scheduler...");
        siteConfigs = siteConfigHolder.getAll();
        LOG.info(siteConfigs.size() + " site config(s) loaded.");
        rules = new HashMap<String, Rule>();
        for (Rule rule: ruleHolder.getAll()) {
            String pattern = rule.getUrlPattern();
            rules.put(pattern, rule);
            LOG.info("Rule loaded for pattern " + pattern);
        }
    }

    public void reload() {
        siteConfigHolder.flush();
        init();
    }

    public int schedule() throws Exception {
        int scheduled = 0;
        for (SiteConfig siteConfig: siteConfigs) {
            for (Seed seed: siteConfig.getSeeds()) {
                long lastRefresh = storage.getRefresh(seed.getUrl());
                long currentTime = storage.currentTime();
                if (currentTime - lastRefresh < seed.getFrequency())
                    continue;
                LOG.info(seed.getUrl() + " is scheduled for crawling.");
                Context context = new Context();
                storage.putContext(context);
                scheduleNewTask(new Task(context.getContextId(), seed.getUrl(),
                        seed.getParams(), seed.getPriority()));
                scheduled++;
            }
        }
        return scheduled;
    }

    public void stop() {
        isRunning = false;
    }

    public void run() throws Exception {
        isRunning = true;
        while (isRunning) {
            int scheduled = schedule();
            LOG.info(scheduled + " task(s) scheduled.");
            Thread.sleep(30000);
        }
    }

}
