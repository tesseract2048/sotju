package org.tju.so.model.crawler.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Context {

    private String contextId;

    private String siteId;

    private String schemaId;

    private String id;

    private Map<String, Object> parsedValues;

    private transient boolean isIndexing;

    private transient boolean isDeleteing;

    private transient List<Task> newTasks;

    public Context() {
        setContextId(UUID.randomUUID().toString());
        parsedValues = new HashMap<String, Object>();
        newTasks = new ArrayList<Task>();
    }

    public Context(Context parentContext) {
        this();
        setSiteId(parentContext.getSiteId());
        setSchemaId(parentContext.getSchemaId());
        setId(parentContext.getId());
        for (Map.Entry<String, Object> entry: parentContext.getParsedValues()
                .entrySet()) {
            parsedValues.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @return the contextId
     */
    public String getContextId() {
        return contextId;
    }

    /**
     * @param contextId
     *            the contextId to set
     */
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * @param siteId
     *            the siteId to set
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * @return the schemaId
     */
    public String getSchemaId() {
        return schemaId;
    }

    /**
     * @param schemaId
     *            the schemaId to set
     */
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
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
     * @return the parsedValues
     */
    public Map<String, Object> getParsedValues() {
        return parsedValues;
    }

    /**
     * @param parsedValues
     *            the parsedValues to set
     */
    public void setParsedValues(Map<String, Object> parsedValues) {
        this.parsedValues = parsedValues;
    }

    /**
     * @return the isIndexing
     */
    public boolean isIndexing() {
        return isIndexing;
    }

    /**
     * @param isIndexing
     *            the isIndexing to set
     */
    public void setIndexing(boolean isIndexing) {
        this.isIndexing = isIndexing;
    }

    /**
     * @return the isDeleteing
     */
    public boolean isDeleteing() {
        return isDeleteing;
    }

    /**
     * @param isDeleteing
     *            the isDeleteing to set
     */
    public void setDeleteing(boolean isDeleteing) {
        this.isDeleteing = isDeleteing;
    }

    /**
     * @return the newTasks
     */
    public List<Task> getNewTasks() {
        return newTasks;
    }

    /**
     * @param newTasks
     *            the newTasks to set
     */
    public void setNewTasks(List<Task> newTasks) {
        this.newTasks = newTasks;
    }

}
