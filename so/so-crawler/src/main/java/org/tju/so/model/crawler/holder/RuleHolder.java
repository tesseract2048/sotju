package org.tju.so.model.crawler.holder;

import org.springframework.stereotype.Service;
import org.tju.so.model.crawler.rule.Rule;
import org.tju.so.model.holder.AbstractElasticHolder;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class RuleHolder extends AbstractElasticHolder<Rule> {

    protected String getIndex() {
        return "crawler";
    }

    protected String getType() {
        return "rule";
    }

    protected Class<Rule> getClazz() {
        return Rule.class;
    }

}
