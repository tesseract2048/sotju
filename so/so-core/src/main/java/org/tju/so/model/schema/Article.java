package org.tju.so.model.schema;

import java.util.Arrays;
import java.util.List;

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
            new Field("title", FieldType.STRING, true),
            new Field("abstract", FieldType.STRING, true),
            new Field("date", FieldType.DATE, false),
            new Field("author", FieldType.STRING, true),
            new Field("column", FieldType.STRING, false),
            new Field("read", FieldType.INTEGER, false),
            new Field("content", FieldType.STRING, true)
        });
    }

    @Override
    public float getRankFactor() {
        return 1.0f;
    }

}
