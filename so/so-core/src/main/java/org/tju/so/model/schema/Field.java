package org.tju.so.model.schema;

import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Field {

    private String name;

    private FieldType type;

    private boolean isDefault;

    private boolean isAnalysed;

    private boolean isKeyword;

    private float boost;

    private List<Field> childFields;

    public Field() {

    }

    public Field(String name, FieldType type, boolean isDefault,
            boolean isAnalysed, boolean isKeyword) {
        this(name, type, isDefault, isAnalysed, isKeyword, 1.0f);
    }

    public Field(String name, FieldType type, boolean isDefault,
            boolean isAnalysed) {
        this(name, type, isDefault, isAnalysed, false, 1.0f);
    }

    public Field(String name, FieldType type) {
        this(name, type, false, false, false, 1.0f);
    }

    public Field(String name, FieldType type, boolean isDefault,
            boolean isAnalysed, boolean isKeyword, float boost) {
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
        this.isAnalysed = isAnalysed;
        this.isKeyword = isKeyword;
        this.boost = boost;
    }

    public Field(String name, FieldType type, List<Field> childFields) {
        this(name, type, false, false, false, 1.0f);
        this.childFields = childFields;
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
     * @return the type
     */
    public FieldType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * @return the isDefault
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * @param isDefault
     *            the isDefault to set
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the isAnalysed
     */
    public boolean isAnalysed() {
        return isAnalysed;
    }

    /**
     * @param isAnalysed
     *            the isAnalysed to set
     */
    public void setAnalysed(boolean isAnalysed) {
        this.isAnalysed = isAnalysed;
    }

    /**
     * @return the isKeyword
     */
    public boolean isKeyword() {
        return isKeyword;
    }

    /**
     * @param isKeyword
     *            the isKeyword to set
     */
    public void setKeyword(boolean isKeyword) {
        this.isKeyword = isKeyword;
    }

    /**
     * @return the boost
     */
    public float getBoost() {
        return boost;
    }

    /**
     * @param boost
     *            the boost to set
     */
    public void setBoost(float boost) {
        this.boost = boost;
    }

    /**
     * @return the childFields
     */
    public List<Field> getChildFields() {
        return childFields;
    }

    /**
     * @param childFields
     *            the childFields to set
     */
    public void setChildFields(List<Field> childFields) {
        this.childFields = childFields;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Field [name=" + name + ", type=" + type + ", isDefault="
                + isDefault + ", isAnalysed=" + isAnalysed + ", boost=" + boost
                + "]";
    }

}
