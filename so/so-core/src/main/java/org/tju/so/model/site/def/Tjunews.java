package org.tju.so.model.site.def;

import org.tju.so.model.site.Site;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Tjunews extends Site {

    public Tjunews() {
        setId("tjunews");
        setName("天津大学新闻网");
        setHomeUrl("http://www.tju.edu.cn/newscenter/");
        setRankingFactors(1.0f);
    }
}
