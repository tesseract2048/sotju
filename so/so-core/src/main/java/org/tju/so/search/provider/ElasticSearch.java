package org.tju.so.search.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionFuzzyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.ObjectHelper;
import org.tju.so.model.entity.Entity;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;
import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;
import org.tju.so.node.ElasticClientInvoker;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;
import org.tju.so.search.context.QueryFilter;
import org.tju.so.search.context.ResultItem;
import org.tju.so.util.LanguageUtil;
import org.tju.so.util.Pair;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class ElasticSearch extends ElasticClientInvoker implements
        SearchProvider {

    private static final String SYSTEM_SITE = "system";

    private static final String COMPLETION_SCHEMA = "completion";

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
        // TODO
        requestBuilder.setQuery(QueryBuilders.functionScoreQuery(QueryBuilders
                .queryString(query.getQuery())));
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

    @SuppressWarnings("unchecked")
    private List<Pair<String, String>> makeCompletions(List<Field> fields,
            Map<String, Object> values) {
        List<Pair<String, String>> completions = new ArrayList<Pair<String, String>>();
        for (Field field: fields) {
            Object value = values.get(field.getName());
            if (value == null)
                continue;
            if (field.getType() == FieldType.OBJECT) {
                completions.addAll(makeCompletions(field.getChildFields(),
                        (Map<String, Object>) value));
            } else if (field.getType() == FieldType.ARRAY) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) value;
                for (Map<String, Object> item: items) {
                    completions.addAll(makeCompletions(field.getChildFields(),
                            item));
                }
            } else if (field.isKeyword()) {
                String text = value.toString();
                completions.add(new Pair<String, String>(text, text));
                if (LanguageUtil.hasUnicode(text)) {
                    completions.add(new Pair<String, String>(LanguageUtil
                            .chinese2Pinyin(text), text));
                }
            }
        }
        return completions;
    }

    private List<Pair<String, String>> makeCompletions(Entity entity) {
        return makeCompletions(entity.getSchema().getFields(),
                entity.getFieldValues());
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
            List<Pair<String, String>> completions = makeCompletions(entity);
            LOG.info("Completions: " + completions);
            for (int i = 0; i < completions.size(); i++) {
                Pair<String, String> completion = completions.get(i);
                String completionId = String.format("%s_%s_%s_%d", entity
                        .getSite().getId(), entity.getSchema().getId(), entity
                        .getId(), i);
                XContentBuilder completionSource;
                try {
                    completionSource = XContentFactory.jsonBuilder()
                            .startObject().startObject("suggest")
                            .field("input", completion.getKey())
                            .startObject("payload")
                            .field("text", completion.getValue()).endObject()
                            .endObject().endObject();
                } catch (IOException e) {
                    LOG.error("Failed to build completion source", e);
                    return false;
                }
                bulkRequest.add(client.prepareIndex(SYSTEM_SITE,
                        COMPLETION_SCHEMA, completionId).setSource(
                        completionSource));
            }
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
        return updateSite(site.getId());
    }

    public boolean updateSite(String name) {
        LOG.info("Updating site " + name + "...");
        if (!indices.prepareExists(name).execute().actionGet().isExists())
            indices.prepareCreate(name).execute().actionGet();
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

    @Override
    public boolean initIndices() {
        LOG.info("Running first-time initialization...");
        try {
            updateSite(SYSTEM_SITE);
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject().startObject(COMPLETION_SCHEMA)
                    .startObject("properties");
            mapping.startObject("suggest").field("type", "completion")
                    .field("index_analyzer", "simple")
                    .field("search_analyzer", "simple").field("payloads", true);
            mapping.endObject().endObject().endObject();
            PutMappingResponse response = indices
                    .preparePutMapping(SYSTEM_SITE).setType(COMPLETION_SCHEMA)
                    .setSource(mapping).execute().actionGet();
            return response.isAcknowledged();
        } catch (Exception e) {
            LOG.error("Failed to run first-time initialization", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getCompletions(String keyword, int limit) {
        LOG.info("Complete request: " + keyword);
        List<String> completions = new ArrayList<String>();
        SuggestResponse response = client
                .prepareSuggest(SYSTEM_SITE)
                .addSuggestion(
                        new CompletionSuggestionFuzzyBuilder(COMPLETION_SCHEMA)
                                .text(keyword).field("suggest").size(limit)
                                .setFuzziness(Fuzziness.AUTO)).execute()
                .actionGet();
        List<Entry> entries = (List<Entry>) response.getSuggest()
                .getSuggestion(COMPLETION_SCHEMA).getEntries();
        for (Entry entry: entries) {
            for (Option option: entry.getOptions()) {
                completions
                        .add(option.getPayloadAsMap().get("text").toString());
            }
        }
        return completions;
    }

}
