package org.tju.so.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.tju.so.model.entity.Entity;
import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.model.schema.Schema;
import org.tju.so.search.context.ResultItem;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class SearchUtil {

    public static final String BOOST_FIELD = "docBoost";

    public static final float BOOST_MIN = 0.01f;

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
                completions.add(new Pair<String, String>(text, text));
                if (LanguageUtil.hasUnicode(text)) {
                    completions.add(new Pair<String, String>(LanguageUtil
                            .chinese2Pinyin(text), text));
                }
            }
        }
        return completions;
    }

    public static List<Pair<String, String>> makeCompletions(Entity entity) {
        return makeCompletions(entity.getSchema().getFields(),
                entity.getFieldValues());
    }

    private static void buildFieldProperties(XContentBuilder mapping,
            List<Field> fields) throws IOException {
        for (Field field: fields) {
            buildFieldProperties(mapping, field);
        }
    }

    private static void buildFieldProperties(XContentBuilder mapping,
            Field field) throws IOException {
        mapping.startObject(field.getName());
        switch (field.getType()) {
            case INTEGER:
                mapping.field("type", "long");
                break;
            case FLOAT:
                mapping.field("type", "double");
                break;
            case STRING:
                mapping.field("type", "string");
                break;
            case DATE:
                mapping.field("type", "date");
                break;
            case BOOLEAN:
                mapping.field("type", "boolean");
                break;
            case OBJECT:
                mapping.field("type", "object");
            case ARRAY:
                mapping.startObject("properties");
                buildFieldProperties(mapping, field.getChildFields());
                mapping.endObject();
            default:
        }
        if (field.isAnalysed()) {
            mapping.field("index", "analyzed");
            mapping.field("analyzer", "ik");
        } else {
            mapping.field("index", "not_analyzed");
        }
        if (field.isDefault()) {
            mapping.field("include_in_all", true);
        } else {
            mapping.field("include_in_all", false);
        }
        mapping.field("boost", field.getBoost());
        mapping.field("store", true);
        mapping.endObject();
    }

    public static XContentBuilder buildSchemaMapping(Schema schema)
            throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                .startObject(schema.getId()).startObject("_source")
                .field("enabled", true).endObject().startObject("_all")
                .field("enabled", true).endObject().startObject("properties");
        mapping.startObject(BOOST_FIELD);
        mapping.field("type", "double");
        mapping.field("index", "not_analyzed");
        mapping.field("include_in_all", false);
        mapping.field("store", true);
        mapping.endObject();
        buildFieldProperties(mapping, schema.getFields());
        mapping.endObject().endObject().endObject();
        return mapping;
    }

    public static double calcuateBoost(Entity entity) {
        Schema schema = entity.getSchema();
        String expr = schema.getDocumentRankExpr();
        if (StringUtils.isEmpty(expr))
            return 1.0f;
        String docJson = new Gson().toJson(entity.getFieldValues());
        String script = "var doc = " + docJson + ";\n" + expr;
        /* calculate per document boost */
        double boost = (double) ScriptUtil.eval(script);
        if (Double.isInfinite(boost) || Double.isNaN(boost)
                || boost < BOOST_MIN)
            boost = BOOST_MIN;
        /* taking per schema & per site boost factor */
        boost *= schema.getRankFactor() * entity.getSite().getRankingFactors();
        return boost;
    }

    public static QueryBuilder buildQuery(String query) {
        /*
         * FIXME: actually elasticsearch provided this ScoreFunctionBuilder
         * already in 2.0, but script overhead is inevitable for now
         */
        return QueryBuilders.functionScoreQuery(
                QueryBuilders.queryString(query)).add(
                ScoreFunctionBuilders.scriptFunction("_score * doc['"
                        + SearchUtil.BOOST_FIELD + "'].value"));
    }

    public static String wrapEntity(Entity entity) {
        double boost = calcuateBoost(entity);
        entity.getFieldValues().put(SearchUtil.BOOST_FIELD, boost);
        try {
            String document = new Gson().toJson(entity.getFieldValues());
            return document;
        } finally {
            entity.getFieldValues().remove(SearchUtil.BOOST_FIELD);
        }
    }

    public static Map<String, Object> unwrapEntity(Map<String, Object> source,
            ResultItem resultItem) {
        resultItem.setDocBoost((double) source.get(SearchUtil.BOOST_FIELD));
        source.remove(SearchUtil.BOOST_FIELD);
        return source;
    }
}
