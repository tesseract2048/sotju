package org.tju.so.model.schema.def;

import java.util.Arrays;

import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.model.schema.Schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Article extends Schema {

    public Article() {
        setId("article");
        setName("文章");
        setFields(Arrays.asList(new Field[] {
            new Field("title", FieldType.STRING, true, true, true),
            new Field("abstract", FieldType.STRING, true, true),
            new Field("date", FieldType.DATE, false, false),
            new Field("author", FieldType.STRING, true, false, true),
            new Field("column", FieldType.STRING, false, false, true),
            new Field("read", FieldType.INTEGER, false, false),
            new Field("content", FieldType.STRING, true, true),
            new Field("url", FieldType.STRING, false, false)
        }));
        setRankFactor(1.0f);
        setDocumentRankExpr("Math.sqrt(Math.log(parseInt(doc.read)+1))");
    }

}
