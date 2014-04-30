package org.tju.so.search.result;

import org.tju.so.model.entity.Entity;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Item {

    private Entity entity;

    private int position;

    private float score;

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
    public float getScore() {
        return score;
    }

    /**
     * @param score
     *            the score to set
     */
    public void setScore(float score) {
        this.score = score;
    }

}
