package org.tju.so.search.provider;

import java.util.List;

import org.tju.so.model.entity.Entity;
import org.tju.so.model.schema.Schema;
import org.tju.so.search.result.Response;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ElasticSearch implements SearchProvider {

    private Response search(String schema, String query) {
        return null;

    }

    @Override
    public Response search(String query) {
        return search("all", query);
    }

    @Override
    public Response search(Schema schema, String query) {
        return search(schema.getId(), query);
    }

    @Override
    public Response search(List<Schema> schemas, String query) {
        return null;
    }

    @Override
    public void index(Entity entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void cancelIndex(Entity entity) {
        // TODO Auto-generated method stub
        
    }

}
