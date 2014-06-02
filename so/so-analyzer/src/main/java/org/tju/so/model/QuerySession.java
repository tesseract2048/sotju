package org.tju.so.model;

import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class QuerySession {

    public static class ClickItem extends QueryItemIdentity {

        private int position;

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

    private String keyword;

    private List<ClickItem> clicks;

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
        return "QuerySession [keyword=" + keyword + ", clicks=" + clicks + "]";
    }

}
