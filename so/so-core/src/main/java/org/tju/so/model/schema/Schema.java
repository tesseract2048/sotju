package org.tju.so.model.schema;

import java.util.List;

import org.tju.so.model.IdBasedObject;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Schema implements IdBasedObject {

    private String id;

    private String name;

    private float rankFactor;

    private List<Field> fields;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rankFactor
     */
    public float getRankFactor() {
        return rankFactor;
    }

    /**
     * @param rankFactor
     *            the rankFactor to set
     */
    public void setRankFactor(float rankFactor) {
        this.rankFactor = rankFactor;
    }

    /**
     * @return the fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @param fields
     *            the fields to set
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
