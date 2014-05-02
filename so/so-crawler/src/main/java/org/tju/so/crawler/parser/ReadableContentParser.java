package org.tju.so.crawler.parser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tju.so.model.crawler.rule.Extractor;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ReadableContentParser implements Parser {

    private static final Logger LOG = LoggerFactory
            .getLogger(ReadableContentParser.class);

    private String content;

    private static Method namedGroupsMethod;

    private Document dom = null;

    static {
        try {
            namedGroupsMethod = Pattern.class.getDeclaredMethod("namedGroups");
            namedGroupsMethod.setAccessible(true);
        } catch (Exception e) {}
    }

    @Override
    public void init(String mimeType, byte[] data) {
        String charset = "UTF-8";
        if (mimeType.contains("charset=")) {
            charset = mimeType.substring(mimeType.indexOf("charset=") + 8);
        }
        LOG.info("Parsing readable content with " + charset + " charset...");
        try {
            content = IOUtils.toString(data, charset);
        } catch (IOException e) {
            content = "";
        }
        LOG.info(data.length + " byte(s) parsed as " + content.length()
                + " char(s).");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> getNamedGroups(Pattern p) {
        try {
            return (Map<String, Integer>) namedGroupsMethod.invoke(p);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> extract(Extractor extractor) {
        List<Map<String, Object>> mchs = new ArrayList<Map<String, Object>>();
        String pattern = extractor.getPattern();
        switch (extractor.getType()) {
            case REGEX:
                Pattern p = Pattern.compile(pattern);
                Map<String, Integer> namedGroups = getNamedGroups(p);
                Matcher m = p.matcher(content);
                while (m.find()) {
                    Map<String, Object> groups = new HashMap<String, Object>();
                    for (Map.Entry<String, Integer> groupEntry: namedGroups
                            .entrySet()) {
                        groups.put(groupEntry.getKey(),
                                m.group(groupEntry.getValue()));
                    }
                    mchs.add(groups);
                }
                break;
            case DOM:
                if (dom == null)
                    dom = Jsoup.parse(content, "");
                for (Element e: dom.select(pattern)) {
                    Map<String, Object> groups = new HashMap<String, Object>();
                    groups.put("html", e.html());
                    groups.put("text", e.text());
                    for (Attribute attr: e.attributes()) {
                        groups.put(attr.getKey(), attr.getValue());
                    }
                    mchs.add(groups);
                }
                break;
            case WHOLE_TEXT:
                Map<String, Object> groups = new HashMap<String, Object>();
                groups.put("text", content);
                mchs.add(groups);
                break;
            case JSON:
                // TODO
                break;
            case SCRIPT:
                // TODO
                break;
        }
        return mchs;
    }

}
