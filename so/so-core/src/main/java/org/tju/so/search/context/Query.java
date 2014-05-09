package org.tju.so.search.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Description of a query, including keyword and filter, etc.
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Query {

    private String query;

    private String[] schemaIds;

    private String[] siteIds;

    private List<QueryFilter> filters;

    private int start;

    private int limit;

    public Query() {
        schemaIds = new String[0];
        siteIds = new String[0];
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
     * @return the schemaIds
     */
    public String[] getSchemaIds() {
        return schemaIds;
    }

    /**
     * @param schemaIds
     *            the schemaIds to set
     */
    public void setSchemaIds(String[] schemaIds) {
        this.schemaIds = schemaIds;
    }

    /**
     * @return the siteIds
     */
    public String[] getSiteIds() {
        return siteIds;
    }

    /**
     * @param siteIds
     *            the siteIds to set
     */
    public void setSiteIds(String[] siteIds) {
        this.siteIds = siteIds;
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
        return "Query [query=" + query + ", schemaIds=" + schemaIds
                + ", siteIds=" + siteIds + ", filters=" + filters + ", start="
                + start + ", limit=" + limit + "]";
    }

}
