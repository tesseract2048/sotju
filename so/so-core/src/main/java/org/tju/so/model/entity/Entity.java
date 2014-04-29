package org.tju.so.model.entity;

import java.util.Map;

import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

public class Entity {

    private Schema schema;

    private Site site;

    private Map<String, Object> fieldValues;

    /**
     * @return the schema
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * @param schema
     *            the schema to set
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    /**
     * @return the site
     */
    public Site getSite() {
        return site;
    }

    /**
     * @param site
     *            the site to set
     */
    public void setSite(Site site) {
        this.site = site;
    }

    /**
     * @return the fieldValues
     */
    public Map<String, Object> getFieldValues() {
        return fieldValues;
    }

    /**
     * @param fieldValues
     *            the fieldValues to set
     */
    public void setFieldValues(Map<String, Object> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public Object getField(String name) {
        return fieldValues.get(name);
    }

    public void setField(String name, Object value) {
        fieldValues.put(name, value);
    }

}
