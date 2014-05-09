package org.tju.so.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.holder.RuleHolder;
import org.tju.so.model.crawler.holder.SiteConfigHolder;
import org.tju.so.model.crawler.rule.def.EwebInfoListRule;
import org.tju.so.model.crawler.rule.def.EwebNewsDetailRule;
import org.tju.so.model.crawler.rule.def.EwebNewsListRule;
import org.tju.so.model.crawler.rule.def.EwebSiteConfig;
import org.tju.so.model.crawler.rule.def.PtSiteConfig;
import org.tju.so.model.crawler.rule.def.PtTorrentDetailRule;
import org.tju.so.model.crawler.rule.def.PtTorrentDownloadRule;
import org.tju.so.model.crawler.rule.def.PtTorrentListRule;
import org.tju.so.model.crawler.rule.def.SeeworldMoviePageRule;
import org.tju.so.model.crawler.rule.def.SeeworldSiteConfig;
import org.tju.so.model.crawler.rule.def.TjunewsInfoClickRule;
import org.tju.so.model.crawler.rule.def.TjunewsInfoDetailRule;
import org.tju.so.model.crawler.rule.def.TjunewsInfoListRule;
import org.tju.so.model.crawler.rule.def.TjunewsSiteConfig;

/**
 * Import all siteConfigs and rules to cluster
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class RuleImporter {

    private static final Logger LOG = LoggerFactory
            .getLogger(RuleImporter.class);

    @Autowired
    private SiteConfigHolder siteConfigHolder;

    @Autowired
    private RuleHolder ruleHolder;

    public void run() {
        siteConfigHolder.clear();
        siteConfigHolder.put(new EwebSiteConfig());
        siteConfigHolder.put(new PtSiteConfig());
        siteConfigHolder.put(new TjunewsSiteConfig());
        siteConfigHolder.put(new SeeworldSiteConfig());

        ruleHolder.clear();
        ruleHolder.put(new EwebNewsListRule());
        ruleHolder.put(new EwebNewsDetailRule());
        ruleHolder.put(new EwebInfoListRule());
        ruleHolder.put(new PtTorrentListRule());
        ruleHolder.put(new PtTorrentDetailRule());
        ruleHolder.put(new PtTorrentDownloadRule());
        ruleHolder.put(new TjunewsInfoListRule());
        ruleHolder.put(new TjunewsInfoDetailRule());
        ruleHolder.put(new TjunewsInfoClickRule());
        ruleHolder.put(new SeeworldMoviePageRule());

        LOG.info("Done. You may want to notify cluster to reload for changes to take effect immediately.");
    }

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        RuleImporter importer = appContext.getBean(RuleImporter.class);
        importer.run();
    }
}
