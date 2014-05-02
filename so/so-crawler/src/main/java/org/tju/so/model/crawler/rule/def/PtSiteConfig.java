package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.rule.Seed;
import org.tju.so.model.crawler.rule.SiteConfig;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class PtSiteConfig extends SiteConfig {

    public PtSiteConfig() {
        setId("pt");
        setSeeds(Arrays.asList(new Seed[] {
            new Seed("http://pt.tju.edu.cn/torrents.php", 3600,
                    TaskPriority.HIGHER)
        }));
    }

}
