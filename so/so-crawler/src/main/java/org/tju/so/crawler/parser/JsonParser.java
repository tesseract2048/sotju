package org.tju.so.crawler.parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tju.so.model.crawler.rule.Extractor;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class JsonParser implements Parser {

    private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);

    private Object content;

    @Override
    public void init(String mimeType, byte[] data) {
        LOG.info("Parsing json object...");
        try {
            content = new Gson().fromJson(IOUtils.toString(data, "UTF-8"),
                    Object.class);
        } catch (IOException e) {
            content = null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> extract(Extractor extractor) {
        String pattern = extractor.getPattern();
        switch (extractor.getType()) {
            case WHOLE:
                return (List<Map<String, Object>>) content;
            case SCRIPT:
                return ParserUtil.extractWithScript(content, pattern);
            default:
                return null;
        }
    }

}
