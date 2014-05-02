package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.rule.Seed;
import org.tju.so.model.crawler.rule.SiteConfig;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class EwebSiteConfig extends SiteConfig {

    public EwebSiteConfig() {
        setId("eweb");
        setSeeds(Arrays.asList(new Seed[] {
            new Seed("http://e.tju.edu.cn/News/noticeList.do", 86400, TaskPriority.NORMAL)
        }));
    }

}
