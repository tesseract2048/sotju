package org.tju.so.search.log;

import java.util.Date;

import org.tju.so.search.context.Query;

/**
 * Description for individual click log entry
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ClickLogEntry implements LogEntry {

    private static final long serialVersionUID = 1169204586890587732L;

    private Date date;

    private Query query;

    private SearchResultItemLogEntry resultItem;

    @Override
    public EntryType getType() {
        return EntryType.CLICK;
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
     * @return the resultItem
     */
    public SearchResultItemLogEntry getResultItem() {
        return resultItem;
    }

    /**
     * @param resultItem
     *            the resultItem to set
     */
    public void setResultItem(SearchResultItemLogEntry resultItem) {
        this.resultItem = resultItem;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClickLogEntry [date=" + date + ", query=" + query
                + ", resultItem=" + resultItem + "]";
    }

}
