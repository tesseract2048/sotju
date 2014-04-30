package org.tju.so.search.provider;

import java.util.List;

import org.tju.so.model.entity.Entity;
import org.tju.so.model.schema.Schema;
import org.tju.so.search.result.Response;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface SearchProvider {

    Response search(String query);

    Response search(Schema schema, String query);

    Response search(List<Schema> schemas, String query);

    void index(Entity entity);

    void cancelIndex(Entity entity);
}
