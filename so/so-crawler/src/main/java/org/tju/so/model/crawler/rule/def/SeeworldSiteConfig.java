package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.rule.Seed;
import org.tju.so.model.crawler.rule.SiteConfig;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class SeeworldSiteConfig extends SiteConfig {

    public SeeworldSiteConfig() {
        setId("seeworld");
        setSeeds(Arrays
                .asList(new Seed[] {
                    new Seed(
                            "http://see.tju.edu.cn/Movie/getMoviesByPage_json?extId=1&page=1&type=movie",
                            3600, TaskPriority.HIGHER,
                            "extId=1&page=1&type=movie"),
                    new Seed(
                            "http://see.tju.edu.cn/Movie/getMoviesByPage_json?extId=2&page=1&type=movie",
                            3600, TaskPriority.HIGHER,
                            "extId=2&page=1&type=movie"),
                    new Seed(
                            "http://see.tju.edu.cn/Movie/getMoviesByPage_json?extId=3&page=1&type=movie",
                            3600, TaskPriority.HIGHER,
                            "extId=3&page=1&type=movie"),
                    new Seed(
                            "http://see.tju.edu.cn/Movie/getMoviesByPage_json?extId=4&page=1&type=movie",
                            3600, TaskPriority.HIGHER,
                            "extId=4&page=1&type=movie"),
                }));
    }

}
