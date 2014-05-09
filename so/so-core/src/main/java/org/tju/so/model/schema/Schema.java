package org.tju.so.model.schema;

import java.util.List;

import org.tju.so.model.IdBasedObject;

/**
 * Description of schema for a set of entities
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Schema implements IdBasedObject {

    private String id;

    private String name;

    private float rankFactor;

    private List<Field> fields;

    private String documentRankExpr;

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

    public Field getField(String name) {
        for (Field field: getFields())
            if (field.getName().equals(name))
                return field;
        return null;
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

    /**
     * @return the documentRankExpr
     */
    public String getDocumentRankExpr() {
        return documentRankExpr;
    }

    /**
     * @param documentRankExpr
     *            the documentRankExpr to set
     */
    public void setDocumentRankExpr(String documentRankExpr) {
        this.documentRankExpr = documentRankExpr;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Schema [id=" + id + ", name=" + name + ", rankFactor="
                + rankFactor + ", fields=" + fields + ", documentRankExpr="
                + documentRankExpr + "]";
    }

}
