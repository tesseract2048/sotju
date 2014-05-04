package org.tju.so.model.schema.def;

import java.util.Arrays;

import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.model.schema.Schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Torrent extends Schema {

    public Torrent() {
        setId("torrent");
        setName("种子");
        setFields(Arrays.asList(new Field[] {
            new Field("title", FieldType.STRING, true, true, true),
            new Field("type", FieldType.STRING, true, true, true),
            new Field("info", FieldType.STRING, true, true),
            new Field("description", FieldType.STRING, true, true),
            new Field("view", FieldType.INTEGER, false, false),
            new Field("click", FieldType.INTEGER, false, false),
            new Field("download", FieldType.INTEGER, false, false),
            new Field("size", FieldType.STRING, false, false),
            new Field("seeder", FieldType.INTEGER, false, false),
            new Field("leecher", FieldType.INTEGER, false, false),
            new Field("torrentName", FieldType.STRING, true, true, true),
            new Field("files", FieldType.ARRAY, Arrays.asList(new Field[] {
                new Field("name", FieldType.STRING, true, true, true),
                new Field("length", FieldType.FLOAT, false, false)
            }))
        }));
        setRankFactor(1.0f);
        setDocumentRankExpr("Math.log(parseInt(doc.download) + parseInt(doc.seeder)*1.8 + parseInt(doc.leecher)*1.4)");
    }

}
