package org.tju.so.search.log;

import java.util.Date;
import java.util.List;

import org.tju.so.search.context.Query;

/**
 * Description for individual search log entry
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class SearchLogEntry implements LogEntry, SearchActionEntry {

    private static final long serialVersionUID = 5471754518792466967L;

    private Date date;

    private String userIdentifier;
    
    private Query query;

    private long total;

    private long queryTook;

    private List<SearchResultItemLogEntry> resultItems;

    @Override
    public EntryType getType() {
        return EntryType.SEARCH;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
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

    /**
     * @return the resultItems
     */
    public List<SearchResultItemLogEntry> getResultItems() {
        return resultItems;
    }

    /**
     * @param resultItems
     *            the resultItems to set
     */
    public void setResultItems(List<SearchResultItemLogEntry> resultItems) {
        this.resultItems = resultItems;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SearchLogEntry [query=" + query + ", total=" + total
                + ", queryTook=" + queryTook + ", resultItems=" + resultItems
                + "]";
    }

}
