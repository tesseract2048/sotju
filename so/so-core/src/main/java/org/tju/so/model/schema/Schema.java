package org.tju.so.model.schema;

import java.util.List;

import org.tju.so.model.IdBasedObject;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Schema extends IdBasedObject {

    String getId();

    String getName();

    float getRankFactor();

    List<Field> getFields();

}
