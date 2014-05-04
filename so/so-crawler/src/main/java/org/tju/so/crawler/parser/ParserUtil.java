package org.tju.so.crawler.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ParserUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ParserUtil.class);

    private static Method namedGroupsMethod;

    static {
        try {
            namedGroupsMethod = Pattern.class.getDeclaredMethod("namedGroups");
            namedGroupsMethod.setAccessible(true);
        } catch (Exception e) {}
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> getNamedGroups(Pattern p) {
        try {
            return (Map<String, Integer>) namedGroupsMethod.invoke(p);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Map<String, Object>> extractWithRegex(String pattern,
            String content) {
        List<Map<String, Object>> mchs = new ArrayList<Map<String, Object>>();
        Pattern p = Pattern.compile(pattern);
        Map<String, Integer> namedGroups = getNamedGroups(p);
        Matcher m = p.matcher(content);
        while (m.find()) {
            Map<String, Object> groups = new HashMap<String, Object>();
            for (Map.Entry<String, Integer> groupEntry: namedGroups.entrySet()) {
                groups.put(groupEntry.getKey(), m.group(groupEntry.getValue()));
            }
            mchs.add(groups);
        }
        return mchs;
    }

    public static List<Map<String, Object>> extractWithJsoup(Document dom,
            String selector) {
        List<Map<String, Object>> mchs = new ArrayList<Map<String, Object>>();
        for (Element e: dom.select(selector)) {
            Map<String, Object> groups = new HashMap<String, Object>();
            groups.put("html", e.html());
            groups.put("text", e.text());
            for (Attribute attr: e.attributes()) {
                groups.put(attr.getKey(), attr.getValue());
            }
            mchs.add(groups);
        }
        return mchs;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> extractWithScript(Object content,
            String script) {
        ScriptEngine engine = new ScriptEngineManager()
                .getEngineByName("JavaScript");
        try {
            String contentJson = new Gson().toJson(content);
            String execute = "function udf(content) {\n" + script
                    + ";};\n" + "JSON.stringify(udf(" + contentJson
                    + "));";
            Object eval = engine.eval(execute);
            return new Gson().fromJson(eval.toString(), List.class);
        } catch (ScriptException e) {
            LOG.error("Script execution failed", e);
            return new ArrayList<Map<String, Object>>();
        }
    }

}
