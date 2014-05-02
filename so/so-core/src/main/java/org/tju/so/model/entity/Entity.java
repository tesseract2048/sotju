package org.tju.so.model.entity;

import java.util.Map;

import org.tju.so.model.IdBasedObject;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Entity implements IdBasedObject {

    private transient Schema schema;

    private transient Site site;

    private String schemaId;

    private String siteId;

    private String id;

    private Map<String, Object> fieldValues;

    public Entity() {

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

    /**
     * @return the schemaId
     */
    public String getSchemaId() {
        return schemaId;
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return siteId;
    }

}
