package org.tju.so.model.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tju.so.model.IdBasedObject;
import org.tju.so.node.ElasticClientInvoker;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public abstract class AbstractElasticHolder<T extends IdBasedObject> extends
        ElasticClientInvoker implements ModelHolder<T> {

    protected static final Logger LOG = LoggerFactory
            .getLogger(AbstractElasticHolder.class);

    protected abstract String getIndex();

    protected abstract String getType();

    protected abstract Class<T> getClazz();

    private volatile Map<String, T> cachedObjects;

    public AbstractElasticHolder() {
        flush();
    }

    public void clear() {
        LOG.info("Cleaning up model holder " + getIndex() + "/" + getType() + "...");
        client.prepareDeleteByQuery(getIndex()).setTypes(getType())
                .setQuery(QueryBuilders.queryString("*:*")).execute()
                .actionGet();
    }

    @PostConstruct
    public void init() {
        LOG.info("Initializing model holder " + getIndex() + "/" + getType() + "...");
        IndicesAdminClient indices = client.admin().indices();
        if (!indices.prepareExists(getIndex()).execute().actionGet().isExists()) {
            indices.prepareCreate(getIndex()).execute().actionGet();
        }
    }

    @Override
    public void flush() {
        LOG.info("Flushing model holder " + getIndex() + "/" + getType() + "...");
        cachedObjects = new HashMap<String, T>();
    }

    @Override
    public void put(T model) {
        LOG.info("Updating " + model.getId() + " in model holder " + getIndex() + "/" + getType() + "...");
        cachedObjects.put(model.getId(), model);
        String document = new Gson().toJson(model);
        client.prepareIndex(getIndex(), getType(), model.getId())
                .setSource(document).execute().actionGet();
    }

    @Override
    public T get(String id) {
        if (!cachedObjects.containsKey(id)) {
            synchronized (this) {
                if (!cachedObjects.containsKey(id)) {
                    cachedObjects.put(id, new Gson().fromJson(client
                            .prepareGet(getIndex(), getType(), id).execute()
                            .actionGet().getSourceAsString(), getClazz()));
                }
            }
        }
        return cachedObjects.get(id);
    }

    @Override
    public List<T> getAll() {
        List<T> items = new ArrayList<T>();
        SearchHits hits = client.prepareSearch(getIndex()).setTypes(getType())
                .setQuery(QueryBuilders.queryString("*:*")).execute()
                .actionGet().getHits();
        for (SearchHit hit: hits) {
            T item = new Gson().fromJson(hit.getSourceAsString(), getClazz());
            cachedObjects.put(item.getId(), item);
            items.add(item);
        }
        return items;
    }

}
