package org.tju.so.search.log;

import java.util.Date;

import org.tju.so.model.DocumentIdentity;
import org.tju.so.search.context.Query;

/**
 * Description for individual click log entry
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ClickLogEntry extends DocumentIdentity implements LogEntry {

    private static final long serialVersionUID = 1169204586890587732L;

    private Date date;

    private String userIdentifier;

    private Query query;

    private int position;

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
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClickLogEntry [date=" + date + ", userIdentifier="
                + userIdentifier + ", query=" + query + ", schemaId="
                + schemaId + ", siteId=" + siteId + ", id=" + id
                + ", position=" + position + "]";
    }

}
