package org.tju.so.model.holder;

import org.springframework.stereotype.Service;
import org.tju.so.model.schema.Schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class SchemaHolder extends AbstractElasticHolder<Schema> {

    protected String getIndex() {
        return "model";
    }

    protected String getType() {
        return "schema";
    }

    protected Class<Schema> getClazz() {
        return Schema.class;
    }

}
