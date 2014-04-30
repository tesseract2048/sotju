package org.tju.so.search.context;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public abstract class QueryFilter {

    public enum Type {
        PREFIX, RANGE, IN
    };

    private String field;

    private Type type;

    private String value;

    public QueryFilter() {

    }

    public QueryFilter(String field, Type type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
