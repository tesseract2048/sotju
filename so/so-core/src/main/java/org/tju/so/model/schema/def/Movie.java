package org.tju.so.model.schema.def;

import java.util.Arrays;

import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.model.schema.Schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Movie extends Schema {

    public Movie() {
        setId("movie");
        setName("影片");
        setFields(Arrays.asList(new Field[] {
            new Field("title", FieldType.STRING, true, true, true),
            new Field("description", FieldType.STRING, true, true),
            new Field("image", FieldType.STRING, false, false),
            new Field("view", FieldType.INTEGER, false, false),
            new Field("comment", FieldType.INTEGER, false, false),
            new Field("addTime", FieldType.DATE, false, false),
            new Field("url", FieldType.STRING, false, false),
        }));
        setRankFactor(1.0f);
        setDocumentRankExpr("Math.sqrt(Math.log(parseInt(doc.view) + parseInt(doc.comment)*2 + 1))");
    }

}
