package org.tju.so.model.holder;

import org.springframework.stereotype.Service;
import org.tju.so.model.site.Site;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class SiteHolder extends AbstractElasticHolder<Site> {

    protected String getIndex() {
        return "model";
    }

    protected String getType() {
        return "site";
    }

    protected Class<Site> getClazz() {
        return Site.class;
    }

}
