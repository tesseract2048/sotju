package org.tju.so.model.site.def;

import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

public class Eweb implements Site {

    @Override
    public String getId() {
        return "eweb";
    }

    @Override
    public String getName() {
        return "天津大学办公网";
    }

    @Override
    public String getHomeUrl() {
        return "http://e.tju.edu.cn/";
    }

    @Override
    public float getRankingFactor(Schema schema) {
        return 1.0f;
    }

}
