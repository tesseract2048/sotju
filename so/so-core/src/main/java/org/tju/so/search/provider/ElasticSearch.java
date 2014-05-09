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
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;
import org.tju.so.node.ElasticClientInvoker;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;
import org.tju.so.search.context.QueryFilter;
import org.tju.so.search.context.ResultItem;
import org.tju.so.util.CompletionUtil;
import org.tju.so.util.Pair;
import org.tju.so.util.SearchUtil;

/**
 * Elasticsearch wrapper
 * 
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
        if (query.getSiteIds().length == 0)
            query.setSiteIds(ObjectHelper.extractIds(siteHolder.getAll()));
        if (query.getSchemaIds().length == 0)
            query.setSchemaIds(ObjectHelper.extractIds(schemaHolder.getAll()));
        SearchRequestBuilder requestBuilder = client.prepareSearch(query
                .getSiteIds());
        requestBuilder.setTypes(query.getSchemaIds());
        requestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        requestBuilder.setQuery(SearchUtil.buildQuery(query.getQuery()));
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
            Map<String, Object> values = SearchUtil.unwrapEntity(
                    hit.getSource(), item);
            item.setEntity(new Entity(schemaHolder.get(hit.getType()),
                    siteHolder.get(hit.getIndex()), hit.getId(), values));
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
        return new Context(query, result, response.getHits().totalHits(),
                response.getTookInMillis());
    }

    private boolean indexCompletion(BulkRequestBuilder bulkRequest,
            Entity entity) {
        List<Pair<String, String>> completions = CompletionUtil
                .makeCompletions(entity);
        LOG.debug("Completions: " + completions);
        for (int i = 0; i < completions.size(); i++) {
            Pair<String, String> completion = completions.get(i);
            String completionId = String.format("%s_%s_%s_%d", entity.getSite()
                    .getId(), entity.getSchema().getId(), entity.getId(), i);
            XContentBuilder completionSource;
            try {
                completionSource = XContentFactory.jsonBuilder().startObject()
                        .startObject("suggest")
                        .field("input", completion.getKey())
                        .startObject("payload")
                        .field("text", completion.getValue()).endObject()
                        .endObject().endObject();
            } catch (IOException e) {
                LOG.error("Failed to build completion source", e);
                return false;
            }
            bulkRequest.add(client.prepareIndex(SYSTEM_SITE, COMPLETION_SCHEMA,
                    completionId).setSource(completionSource));
        }
        return true;
    }

    @Override
    public boolean index(Entity... entities) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (Entity entity: entities) {
            LOG.info("Indexing " + entity.getSite().getId() + "/"
                    + entity.getSchema().getId() + "/" + entity.getId() + "...");
            String document = SearchUtil.wrapEntity(entity);
            bulkRequest.add(client.prepareIndex(entity.getSite().getId(),
                    entity.getSchema().getId(), entity.getId()).setSource(
                    document));
            if (!indexCompletion(bulkRequest, entity)) {
                return false;
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

    @Override
    public boolean updateSchema(Site[] sites, Schema schema) {
        LOG.info("Updating schema " + schema.getId() + "...");
        try {
            PutMappingResponse response = indices
                    .preparePutMapping(
                            ObjectHelper.extractIds((Object[]) sites))
                    .setType(schema.getId()).setIgnoreConflicts(true)
                    .setSource(SearchUtil.buildSchemaMapping(schema)).execute()
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
        LOG.info("Completion request: " + keyword);
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
