package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.rule.Seed;
import org.tju.so.model.crawler.rule.SiteConfig;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class TjunewsSiteConfig extends SiteConfig {

    public TjunewsSiteConfig() {
        setId("tjunews");
        setSeeds(Arrays
                .asList(new Seed[] {
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/headline/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/teaching/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/foreign_affairs/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/alumni/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/personnel/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/party_affairs/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/personnel/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/student/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/soundm/index.htm",
                            86400, TaskPriority.NORMAL),
                    new Seed(
                            "http://www.tju.edu.cn/newscenter/other/index.htm",
                            86400, TaskPriority.NORMAL)
                }));
    }

}
