package org.tju.so.search.context;

import java.util.ArrayList;
import java.util.List;

import org.tju.so.model.ObjectHelper;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Query {

    private String query;

    private transient List<Schema> schemas;

    private transient List<Site> sites;

    private List<QueryFilter> filters;

    private int start;

    private int limit;

    public Query() {
        schemas = new ArrayList<Schema>();
        sites = new ArrayList<Site>();
        filters = new ArrayList<QueryFilter>();
        start = 0;
        limit = 10;
    }

    public Query(String query) {
        this();
        this.query = query;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query
     *            the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the schemas
     */
    public List<Schema> getSchemas() {
        return schemas;
    }

    /**
     * @param schemas
     *            the schemas to set
     */
    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    /**
     * @return the sites
     */
    public List<Site> getSites() {
        return sites;
    }

    /**
     * @param sites
     *            the sites to set
     */
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    /**
     * @return the filters
     */
    public List<QueryFilter> getFilters() {
        return filters;
    }

    /**
     * @param filters
     *            the filters to set
     */
    public void setFilters(List<QueryFilter> filters) {
        this.filters = filters;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit
     *            the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Query [query=" + query + ", schemas="
                + ObjectHelper.extractIds(schemas) + ", sites="
                + ObjectHelper.extractIds(sites) + ", filters=" + filters
                + ", start=" + start + ", limit=" + limit + "]";
    }

}
