package org.tju.so.search.log;

import java.util.Date;
import java.util.List;

/**
 * Description for individual auto-complete log entry
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class CompleteLogEntry implements LogEntry {

    private static final long serialVersionUID = 3850448671899997870L;

    private Date date;

    private String userIdentifier;

    private String keyword;

    private int limit;

    private List<String> completions;

    @Override
    public EntryType getType() {
        return EntryType.COMPLETE;
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
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword
     *            the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    /**
     * @return the completions
     */
    public List<String> getCompletions() {
        return completions;
    }

    /**
     * @param completions
     *            the completions to set
     */
    public void setCompletions(List<String> completions) {
        this.completions = completions;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CompleteLogEntry [date=" + date + ", keyword=" + keyword
                + ", limit=" + limit + ", completions=" + completions + "]";
    }

}
