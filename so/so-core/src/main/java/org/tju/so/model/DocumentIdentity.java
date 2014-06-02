package org.tju.so.model;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public abstract class DocumentIdentity {

    protected String siteId;

    protected String schemaId;

    protected String id;

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

    @Override
    public String toString() {
        return "DocumentIdentity [siteId=" + siteId + ", schemaId=" + schemaId
                + ", id=" + id + "]";
    }

}
