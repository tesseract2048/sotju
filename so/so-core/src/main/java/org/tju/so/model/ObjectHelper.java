package org.tju.so.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ObjectHelper {

    public static List<String> extractIdsAsList(Object... objects) {
        List<String> ids = new ArrayList<String>();
        for (Object object: objects) {
            ids.add(((IdBasedObject) object).getId());
        }
        return ids;
    }

    public static String[] extractIds(Object... objects) {
        return extractIdsAsList(objects).toArray(new String[0]);
    }

    @SuppressWarnings("rawtypes")
    public static String[] extractIds(List objects) {
        return extractIdsAsList(objects.toArray()).toArray(new String[0]);
    }

    @SuppressWarnings("rawtypes")
    public static List<String> extractIdsAsList(List objects) {
        return extractIdsAsList(objects.toArray());
    }

}
