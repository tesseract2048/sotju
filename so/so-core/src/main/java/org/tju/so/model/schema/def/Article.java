package org.tju.so.model.schema.def;

import java.util.Arrays;
import java.util.List;

import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.model.schema.Schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Article implements Schema {

    @Override
    public String getId() {
        return "article";
    }

    @Override
    public String getName() {
        return "文章";
    }

    @Override
    public List<Field> getFields() {
        return Arrays.asList(new Field[] {
            new Field("title", FieldType.STRING, true, true),
            new Field("abstract", FieldType.STRING, true, true),
            new Field("date", FieldType.DATE, false, false),
            new Field("author", FieldType.STRING, true, false),
            new Field("column", FieldType.STRING, false, false),
            new Field("read", FieldType.INTEGER, false, false),
            new Field("content", FieldType.STRING, true, true)
        });
    }

    @Override
    public float getRankFactor() {
        return 1.0f;
    }

}
