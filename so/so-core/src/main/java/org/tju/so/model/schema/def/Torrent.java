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
            new Field("title", FieldType.STRING, true, true),
            new Field("type", FieldType.STRING, true, true),
            new Field("info", FieldType.STRING, true, true),
            new Field("description", FieldType.STRING, true, true),
            new Field("view", FieldType.INTEGER, false, false),
            new Field("click", FieldType.INTEGER, false, false),
            new Field("download", FieldType.INTEGER, false, false),
            new Field("size", FieldType.INTEGER, false, false),
            new Field("seeder", FieldType.INTEGER, false, false),
            new Field("leecher", FieldType.INTEGER, false, false),
            new Field("hash", FieldType.STRING, false, false),
            new Field("torrentName", FieldType.STRING, true, true)
        }));
        setRankFactor(1.0f);
    }

}
