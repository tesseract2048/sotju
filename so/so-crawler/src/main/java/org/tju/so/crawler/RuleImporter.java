package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.model.crawler.holder.RuleHolder;
import org.tju.so.model.crawler.holder.SiteConfigHolder;
import org.tju.so.model.crawler.rule.def.EwebNewsDetailRule;
import org.tju.so.model.crawler.rule.def.EwebNoticeListRule;
import org.tju.so.model.crawler.rule.def.EwebSiteConfig;
import org.tju.so.model.crawler.rule.def.PtSiteConfig;
import org.tju.so.model.crawler.rule.def.PtTorrentDetailRule;
import org.tju.so.model.crawler.rule.def.PtTorrentDownloadRule;
import org.tju.so.model.crawler.rule.def.PtTorrentListRule;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class RuleImporter {

    @Autowired
    private SiteConfigHolder siteConfigHolder;

    @Autowired
    private RuleHolder ruleHolder;

    public void run() {
        siteConfigHolder.clear();
        siteConfigHolder.put(new EwebSiteConfig());
        siteConfigHolder.put(new PtSiteConfig());

        ruleHolder.clear();
        ruleHolder.put(new EwebNoticeListRule());
        ruleHolder.put(new EwebNewsDetailRule());
        ruleHolder.put(new PtTorrentListRule());
        ruleHolder.put(new PtTorrentDetailRule());
        ruleHolder.put(new PtTorrentDownloadRule());
    }

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        RuleImporter importer = appContext.getBean(RuleImporter.class);
        importer.run();
    }
}
