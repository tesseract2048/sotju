package org.tju.so.model;

public class QueryItemIdentity extends DocumentIdentity {

    public QueryItemIdentity() {}

    public QueryItemIdentity(String siteId, String schemaId, String id) {
        setSiteId(siteId);
        setSchemaId(schemaId);
        setId(id);
    }

    public QueryItemIdentity(DocumentIdentity identity) {
        setSiteId(identity.getSiteId());
        setSchemaId(identity.getSchemaId());
        setId(identity.getId());
    }

    @Override
    public int hashCode() {
        return siteId.hashCode() + schemaId.hashCode() + id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QueryItemIdentity))
            return false;
        QueryItemIdentity t = (QueryItemIdentity) obj;
        if (!t.getSchemaId().equals(schemaId))
            return false;
        if (!t.getSiteId().equals(siteId))
            return false;
        if (!t.getId().equals(id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return siteId + "," + schemaId + "," + id;
    }

    public static QueryItemIdentity valueOf(String s) {
        String[] cols = s.split(",");
        return new QueryItemIdentity(cols[0], cols[1], cols[2]);
    }
}
