package org.tju.so.crawler.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.holder.SiteConfigHolder;
import org.tju.so.model.crawler.rule.Seed;
import org.tju.so.model.crawler.rule.SiteConfig;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class Scheduler {

    private static final Logger LOG = LoggerFactory
            .getLogger(Scheduler.class);

    @Autowired
    private SiteConfigHolder siteConfigHolder;

    @Autowired
    private Storage storage;

    private List<SiteConfig> siteConfigs;

    private volatile boolean isRunning;

    @PostConstruct
    public void init() {
        LOG.info("Initializing crawler scheduler...");
        siteConfigs = siteConfigHolder.getAll();
        LOG.info(siteConfigs.size() + " site config(s) loaded.");
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
                storage.pushTask(new Task(context.getContextId(),
                        seed.getUrl(), seed.getPriority()));
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
