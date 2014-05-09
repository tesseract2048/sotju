package org.tju.so.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tju.so.model.entity.Entity;
import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;

/**
 * Utility for auto completion during search
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class CompletionUtil {

    private final static Pattern TEXT_EXTRACTION = Pattern
            .compile("\\[([^\\]]+)\\]");

    /**
     * Make completions for a single corpus of words
     * 
     * @param text
     * @return
     */
    private static List<Pair<String, String>> makeCompletions(String text) {
        int found = 0;
        List<Pair<String, String>> completions = new ArrayList<Pair<String, String>>();
        Matcher mchs = TEXT_EXTRACTION.matcher(text);
        while (mchs.find()) {
            completions.addAll(makeCompletions(mchs.group(1)));
            found++;
        }
        if (found == 0) {
            completions.add(new Pair<String, String>(text, text));
            if (LanguageUtil.hasUnicode(text)) {
                completions.add(new Pair<String, String>(LanguageUtil
                        .chinese2Pinyin(text), text));
            }
        }
        return completions;
    }

    /**
     * Make completions for specified fields and its values
     * 
     * @param fields
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    private static List<Pair<String, String>> makeCompletions(
            List<Field> fields, Map<String, Object> values) {
        List<Pair<String, String>> completions = new ArrayList<Pair<String, String>>();
        for (Field field: fields) {
            Object value = values.get(field.getName());
            if (value == null)
                continue;
            if (field.getType() == FieldType.OBJECT) {
                completions.addAll(makeCompletions(field.getChildFields(),
                        (Map<String, Object>) value));
            } else if (field.getType() == FieldType.ARRAY) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) value;
                for (Map<String, Object> item: items) {
                    completions.addAll(makeCompletions(field.getChildFields(),
                            item));
                }
            } else if (field.isKeyword()) {
                String text = value.toString();
                completions.addAll(makeCompletions(text));
            }
        }
        return completions;
    }

    /**
     * Make completion for specified entity
     * 
     * @param entity
     * @return
     */
    public static List<Pair<String, String>> makeCompletions(Entity entity) {
        return makeCompletions(entity.getSchema().getFields(),
                entity.getFieldValues());
    }

}
