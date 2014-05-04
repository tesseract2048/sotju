package org.tju.so.search.context;

import org.tju.so.model.entity.Entity;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ResultItem {

    private Entity entity;

    private int position;

    private double score;

    private double docBoost;

    public ResultItem() {

    }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity
     *            the entity to set
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
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

}
