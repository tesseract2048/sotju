package org.tju.so.search.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.ObjectHelper;
import org.tju.so.model.entity.Entity;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;
import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;
import org.tju.so.node.ElasticClientInvoker;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;
import org.tju.so.search.context.QueryFilter;
import org.tju.so.search.context.ResultItem;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class ElasticSearch extends ElasticClientInvoker implements
        SearchProvider {

    private static final Logger LOG = LoggerFactory
            .getLogger(ElasticSearch.class);

    @Autowired
    private SchemaHolder schemaHolder;

    @Autowired
    private SiteHolder siteHolder;

    private IndicesAdminClient indices;

    @PostConstruct
    public void init() {
        indices = client.admin().indices();
    }

    private FilterBuilder makeFilterBuilder(List<QueryFilter> filters) {
        List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
        for (QueryFilter filter: filters) {
            FilterBuilder item = null;
            String[] cols;
            switch (filter.getType()) {
                case PREFIX:
                    item = FilterBuilders.prefixFilter(filter.getField(),
                            filter.getValue());
                    break;
                case RANGE:
                    cols = filter.getValue().split(",");
                    double start = Double.parseDouble(cols[0]);
                    double end = Double.parseDouble(cols[1]);
                    item = FilterBuilders.rangeFilter(filter.getField())
                            .from(start).to(end);
                    break;
                case IN:
                    cols = filter.getValue().split(",");
                    item = FilterBuilders.inFilter(filter.getField(), cols);
                default:
            }
            if (item != null)
                filterBuilders.add(item);
        }
        return FilterBuilders.andFilter(filterBuilders
                .toArray(new FilterBuilder[0]));
    }

    private SearchRequestBuilder setupRequestBuilder(Query query) {
        if (query.getSites().size() == 0)
            query.setSites(siteHolder.getAll());
        if (query.getSchemas().size() == 0)
            query.setSchemas(schemaHolder.getAll());
        SearchRequestBuilder requestBuilder = client.prepareSearch(ObjectHelper
                .extractIds(query.getSites()));
        requestBuilder.setTypes(ObjectHelper.extractIds(query.getSchemas()));
        requestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        requestBuilder.setQuery(QueryBuilders.queryString(query.getQuery()));
        requestBuilder.setFrom(query.getStart());
        requestBuilder.setSize(query.getLimit());
        requestBuilder.setPostFilter(makeFilterBuilder(query.getFilters()));
        return requestBuilder;
    }

    private List<ResultItem> buildResult(SearchResponse response) {
        int position = 0;
        List<ResultItem> result = new ArrayList<ResultItem>();
        for (SearchHit hit: response.getHits()) {
            ResultItem item = new ResultItem();
            item.setPosition(position++);
            item.setScore(hit.getScore());
            item.setEntity(new Entity(schemaHolder.get(hit.getType()),
                    siteHolder.get(hit.getIndex()), hit.getId(), hit
                            .getSource()));
            result.add(item);
        }
        return result;
    }

    @Override
    public Context search(Query query) {
        LOG.info("Search request: " + query.toString());
        SearchRequestBuilder request = setupRequestBuilder(query);
        SearchResponse response = request.execute().actionGet();
        List<ResultItem> result = buildResult(response);
        return new Context(query, result, response.getTookInMillis());
    }

    @Override
    public boolean index(Entity... entities) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (Entity entity: entities) {
            LOG.info("Indexing " + entity.getSite().getId() + "/"
                    + entity.getSchema().getId() + "/" + entity.getId() + "...");
            String document = new Gson().toJson(entity.getFieldValues());
            bulkRequest.add(client.prepareIndex(entity.getSite().getId(),
                    entity.getSchema().getId(), entity.getId()).setSource(
                    document));
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse item: bulkResponse.getItems()) {
                if (item.isFailed()) {
                    LOG.error("Failed to index: " + item.getFailureMessage());
                }
            }

        }
        return !bulkResponse.hasFailures();
    }

    @Override
    public boolean delete(Entity entity) {
        LOG.info("Deleteing " + entity.getSite().getId() + "/"
                + entity.getSchema().getId() + "/" + entity.getId() + "...");
        client.prepareDelete(entity.getSite().getId(),
                entity.getSchema().getId(), entity.getId()).execute()
                .actionGet();
        return true;
    }

    @Override
    public boolean updateSite(Site site) {
        LOG.info("Updating site " + site.getId() + "...");
        if (!indices.prepareExists(site.getId()).execute().actionGet()
                .isExists())
            indices.prepareCreate(site.getId()).execute().actionGet();
        return true;
    }

    private void buildFieldProperties(XContentBuilder mapping,
            List<Field> fields) throws IOException {
        for (Field field: fields) {
            buildFieldProperties(mapping, field);
        }
    }

    private void buildFieldProperties(XContentBuilder mapping, Field field)
            throws IOException {
        mapping.startObject(field.getName());
        switch (field.getType()) {
            case INTEGER:
                mapping.field("type", "long");
                break;
            case FLOAT:
                mapping.field("type", "double");
                break;
            case STRING:
                mapping.field("type", "string");
                break;
            case DATE:
                mapping.field("type", "date");
                break;
            case BOOLEAN:
                mapping.field("type", "boolean");
                break;
            case OBJECT:
                mapping.field("type", "object");
            case ARRAY:
                mapping.startObject("properties");
                buildFieldProperties(mapping, field.getChildFields());
                mapping.endObject();
            default:
        }
        if (field.isAnalysed()) {
            mapping.field("index", "analyzed");
            mapping.field("analyzer", "ik");
        } else {
            mapping.field("index", "not_analyzed");
        }
        if (field.isDefault()) {
            mapping.field("include_in_all", true);
        } else {
            mapping.field("include_in_all", false);
        }
        mapping.field("boost", field.getBoost());
        mapping.field("store", true);
        mapping.endObject();
    }

    private XContentBuilder buildSchemaMapping(Schema schema)
            throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                .startObject(schema.getId()).startObject("_source")
                .field("enabled", true).endObject().startObject("_all")
                .field("enabled", true).endObject().startObject("properties");
        buildFieldProperties(mapping, schema.getFields());
        mapping.endObject().endObject().endObject();
        return mapping;
    }

    @Override
    public boolean updateSchema(Site[] sites, Schema schema) {
        LOG.info("Updating schema " + schema.getId() + "...");
        try {
            PutMappingResponse response = indices
                    .preparePutMapping(
                            ObjectHelper.extractIds((Object[]) sites))
                    .setType(schema.getId())
                    .setSource(buildSchemaMapping(schema)).execute()
                    .actionGet();
            if (!response.isAcknowledged()) {
                LOG.error("Failed to update schema");
            }
            return response.isAcknowledged();
        } catch (Exception e) {
            return false;
        }
    }

}
