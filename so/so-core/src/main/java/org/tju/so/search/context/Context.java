package org.tju.so.search.context;

import java.util.List;

/**
 * Description of search context, including its query, result and other
 * important attributes
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Context {

    private String userIdentifier;

    private Query query;

    private List<ResultItem> result;

    private long total;

    private long queryTook;

    public Context() {}

    public Context(Query query, List<ResultItem> result, long total,
            long queryTook) {
        this.query = query;
        this.result = result;
        this.total = total;
        this.queryTook = queryTook;
    }

    /**
     * @return the userIdentifier
     */
    public String getUserIdentifier() {
        return userIdentifier;
    }

    /**
     * @param userIdentifier
     *            the userIdentifier to set
     */
    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
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
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * @return the queryTook
     */
    public long getQueryTook() {
        return queryTook;
    }

    /**
     * @param queryTook
     *            the queryTook to set
     */
    public void setQueryTook(long queryTook) {
        this.queryTook = queryTook;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Context [query=" + query + ", result=" + result + ", total="
                + total + ", queryTook=" + queryTook + "]";
    }

}
