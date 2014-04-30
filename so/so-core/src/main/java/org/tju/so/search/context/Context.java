package org.tju.so.search.context;

import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Context {

    private Query query;

    private List<ResultItem> result;

    private float queryTime;

    public Context() {}

    public Context(Query query, List<ResultItem> result, float queryTime) {
        this.query = query;
        this.result = result;
        this.queryTime = queryTime;
    }

    /**
     * @return the query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * @param query
     *            the query to set
     */
    public void setQuery(Query query) {
        this.query = query;
    }

    /**
     * @return the result
     */
    public List<ResultItem> getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(List<ResultItem> result) {
        this.result = result;
    }

    /**
     * @return the queryTime
     */
    public float getQueryTime() {
        return queryTime;
    }

    /**
     * @param queryTime
     *            the queryTime to set
     */
    public void setQueryTime(float queryTime) {
        this.queryTime = queryTime;
    }

}
