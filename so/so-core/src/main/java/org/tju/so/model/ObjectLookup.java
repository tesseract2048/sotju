package org.tju.so.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ObjectLookup<T> {

    private String packagePath;

    private volatile Map<String, T> cachedObjects;

    public ObjectLookup(String packagePath) {
        this.packagePath = packagePath;
        cachedObjects = new HashMap<String, T>();
    }

    public String formatName(String origName) {
        String name = origName.substring(0, 1).toUpperCase()
                + origName.substring(1).toLowerCase();
        return name;
    }

    @SuppressWarnings("unchecked")
    public T get(String name) {
        name = formatName(name);
        try {
            if (!cachedObjects.containsKey(name)) {
                synchronized (this) {
                    if (!cachedObjects.containsKey(name)) {
                        T object;
                        object = (T) Class.forName(packagePath + "." + name)
                                .newInstance();
                        cachedObjects.put(name, object);
                    }
                }
            }
            return cachedObjects.get(name);
        } catch (Exception e) {
            return null;
        }
    }

}
