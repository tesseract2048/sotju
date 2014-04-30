package org.tju.so.model.holder;

import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface ModelHolder<T> {

    void put(T model);

    T get(String id);

    List<T> getAll();

    void flush();
}
