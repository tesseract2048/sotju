package org.tju.so.model.schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Field {

    private String name;

    private FieldType type;

    private boolean isDefault;

    private boolean isAnalysed;

    private float boost;

    public Field() {

    }

    public Field(String name, FieldType type, boolean isDefault,
            boolean isAnalysed) {
        this(name, type, isDefault, isAnalysed, 1.0f);
    }

    public Field(String name, FieldType type, boolean isDefault,
            boolean isAnalysed, float boost) {
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
        this.boost = boost;
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

}
