package org.tju.so.util;

import java.io.IOException;
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
import org.tju.so.model.schema.Schema;
import org.tju.so.search.context.ResultItem;

import com.google.gson.Gson;

/**
 * Utility for searching, including index building, query building, completion
 * building, etc.
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class SearchUtil {

    public static final String BOOST_FIELD = "docBoost";

    public static final float BOOST_MIN = 0.01f;

    /**
     * Build properties entry in mapping for given fields
     * 
     * @param mapping
     * @param fields
     * @throws IOException
     */
    private static void buildFieldProperties(XContentBuilder mapping,
            List<Field> fields) throws IOException {
        for (Field field: fields) {
            buildFieldProperties(mapping, field);
        }
    }

    /**
     * Build properties entry in mapping for given field
     * 
     * @param mapping
     * @param field
     * @throws IOException
     */
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

    /**
     * Build mapping for given schema
     * 
     * @param schema
     * @throws IOException
     */
    public static XContentBuilder buildSchemaMapping(Schema schema)
            throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                .startObject(schema.getId()).startObject("_source")
                .field("enabled", true).endObject().startObject("_all")
                .field("enabled", true).field("index", "analyzed")
                .field("analyzer", "ik").endObject().startObject("properties");
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

    /**
     * Calculate document boost for given entity. Boost directly influence
     * document ranking during term query.
     * 
     * @param entity
     * @return
     */
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

    /**
     * Build query for specified keyword
     * 
     * @param query
     * @return
     */
    public static QueryBuilder buildQuery(String query) {
        /*
         * FIXME: actually elasticsearch provided this ScoreFunctionBuilder
         * already in 2.0, but script overhead is inevitable for now
         */
        return QueryBuilders.functionScoreQuery(
                QueryBuilders.queryString(query).analyzer("ik")).add(
                ScoreFunctionBuilders.scriptFunction("_score * doc['"
                        + SearchUtil.BOOST_FIELD + "'].value"));
    }

    /**
     * Wrap entity for indexing
     * 
     * @param entity
     * @return
     */
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

    /**
     * Unwrap entity for searching
     * 
     * @param source
     * @param resultItem
     * @return
     */
    public static Map<String, Object> unwrapEntity(Map<String, Object> source,
            ResultItem resultItem) {
        resultItem.setDocBoost((double) source.get(SearchUtil.BOOST_FIELD));
        source.remove(SearchUtil.BOOST_FIELD);
        return source;
    }
}
