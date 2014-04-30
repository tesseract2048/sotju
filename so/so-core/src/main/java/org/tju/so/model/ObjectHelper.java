package org.tju.so.model;

import java.util.ArrayList;
import java.util.List;

import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ObjectHelper {

    private static ObjectLookup<Schema> schemaLookup = new ObjectLookup<Schema>(
            "org.tju.so.model.schema.def");

    private static ObjectLookup<Site> siteLookup = new ObjectLookup<Site>(
            "org.tju.so.model.site.def");

    public static String[] extractIds(Object... objects) {
        List<String> ids = new ArrayList<String>();
        for (Object object: objects) {
            ids.add(((IdBasedObject) object).getId());
        }
        return ids.toArray(new String[0]);
    }

    @SuppressWarnings("rawtypes")
    public static String[] extractIds(List objects) {
        return extractIds(objects.toArray());
    }

    public static Schema getSchema(String id) {
        return schemaLookup.get(id);
    }

    public static Site getSite(String id) {
        return siteLookup.get(id);
    }

}
