package org.tju.so.model.entity;

import java.util.HashMap;
import java.util.Map;

import org.tju.so.model.DocumentIdentity;
import org.tju.so.model.IdBasedObject;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

/**
 * Indexed and stored entity, or say document
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Entity extends DocumentIdentity implements IdBasedObject {

    private transient Schema schema;

    private transient Site site;

    private Map<String, Object> fieldValues;

    public Entity() {
        fieldValues = new HashMap<String, Object>();
    }

    public Entity(Schema schema, Site site, String id,
            Map<String, Object> fieldValues) {
        setSchema(schema);
        setSite(site);
        setId(id);
        setFieldValues(fieldValues);
    }

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
        this.schemaId = schema.getId();
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
        this.siteId = site.getId();
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
