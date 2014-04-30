package org.tju.so.model.entity;

import java.util.Map;

import org.tju.so.model.IdBasedObject;
import org.tju.so.model.ObjectHelper;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Entity implements IdBasedObject {

    private Schema schema;

    private Site site;

    private String id;

    private Map<String, Object> fieldValues;

    public Entity() {

    }

    public Entity(Schema schema, Site site, String id,
            Map<String, Object> fieldValues) {
        this.schema = schema;
        this.site = site;
        this.id = id;
        this.fieldValues = fieldValues;
    }

    public Entity(String schemaId, String siteId, String id,
            Map<String, Object> fieldValues) {
        this.schema = ObjectHelper.getSchema(schemaId);
        this.site = ObjectHelper.getSite(siteId);
        this.id = id;
        this.fieldValues = fieldValues;
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
