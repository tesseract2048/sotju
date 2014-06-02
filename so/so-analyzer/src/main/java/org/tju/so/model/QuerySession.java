package org.tju.so.model;

import java.util.Date;
import java.util.List;

import org.tju.so.search.log.ClickLogEntry;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class QuerySession {

    public static class ClickItem extends QueryItemIdentity {

        private int position;

        public ClickItem() {}

        public ClickItem(DocumentIdentity docId) {
            super(docId);
        }

        public ClickItem(ClickLogEntry entry) {
            super(entry);
            setPosition(entry.getPosition());
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

    }

    private Date startDate;

    private String keyword;

    private List<ClickItem> clicks;

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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
     * @return the clicks
     */
    public List<ClickItem> getClicks() {
        return clicks;
    }

    /**
     * @param clicks
     *            the clicks to set
     */
    public void setClicks(List<ClickItem> clicks) {
        this.clicks = clicks;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QuerySession [startDate=" + startDate + ", keyword=" + keyword
                + ", clicks=" + clicks + "]";
    }

}
