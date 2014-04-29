package org.tju.so.model.schema;

import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Schema {

    String getId();

    String getName();

    float getRankFactor();

    List<Field> getFields();

}
