package org.tju.so.model.crawler.holder;

import org.springframework.stereotype.Service;
import org.tju.so.model.crawler.rule.SiteConfig;
import org.tju.so.model.holder.AbstractElasticHolder;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class SiteConfigHolder extends AbstractElasticHolder<SiteConfig> {

    protected String getIndex() {
        return "crawler";
    }

    protected String getType() {
        return "site";
    }

    protected Class<SiteConfig> getClazz() {
        return SiteConfig.class;
    }

}
