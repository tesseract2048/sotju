package org.tju.so.search.log;

import org.tju.so.model.DocumentIdentity;

/**
 * Description for individual search log entry
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class SearchResultItemLogEntry extends DocumentIdentity implements
        LogEntry {

    private static final long serialVersionUID = 2946112488271679260L;

    private int position;

    private double score;

    private double docBoost;

    @Override
    public EntryType getType() {
        return EntryType.SEARCH_RESULT_ITEM;
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

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score
     *            the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @return the docBoost
     */
    public double getDocBoost() {
        return docBoost;
    }

    /**
     * @param docBoost
     *            the docBoost to set
     */
    public void setDocBoost(double docBoost) {
        this.docBoost = docBoost;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SearchResultItemLogEntry [schemaId=" + schemaId + ", siteId="
                + siteId + ", id=" + id + ", position=" + position + ", score="
                + score + ", docBoost=" + docBoost + "]";
    }

}
