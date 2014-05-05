package org.tju.so.crawler.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    private Document dom = null;

    private String extractCharsetAttribute(String input) {
        String charset = null;
        if (input.contains("charset=")) {
            charset = input.substring(input.indexOf("charset=") + 8);
            if (charset.contains(";"))
                charset = charset.substring(0, charset.indexOf(";"));
            if (charset.contains("'"))
                charset = charset.substring(0, charset.indexOf("'"));
            if (charset.contains("\""))
                charset = charset.substring(0, charset.indexOf("\""));
            if (charset.contains(" "))
                charset = charset.substring(0, charset.indexOf(" "));
        }
        return charset;
    }

    @Override
    public void init(String mimeType, byte[] data) {
        String charset = null;
        charset = extractCharsetAttribute(mimeType);
        if (charset == null) {
            // a simple chardet is performed
            try {
                content = IOUtils.toString(data, "ASCII");
                charset = extractCharsetAttribute(content);
            } catch (IOException e) {}
        }
        if (charset == null) {
            charset = "UTF-8";
            LOG.warn("Mimetype does not contain a valid charset as well as content, continuing with UTF-8.");
        }
        LOG.debug("Parsing readable content with " + charset + " charset...");
        try {
            content = IOUtils.toString(data, charset);
        } catch (IOException e) {
            content = "";
        }
        LOG.info(data.length + " byte(s) parsed as " + content.length()
                + " char(s).");
    }

    @Override
    public List<Map<String, Object>> extract(Extractor extractor) {
        String pattern = extractor.getPattern();
        switch (extractor.getType()) {
            case REGEX:
                return ParserUtil.extractWithRegex(pattern, content);
            case DOM:
                if (dom == null)
                    dom = Jsoup.parse(content, "");
                return ParserUtil.extractWithJsoup(dom, pattern);
            case WHOLE:
                List<Map<String, Object>> mchs = new ArrayList<Map<String, Object>>();
                Map<String, Object> groups = new HashMap<String, Object>();
                groups.put("text", content);
                mchs.add(groups);
                return mchs;
            case SCRIPT:
                return ParserUtil.extractWithScript(content, pattern);
            default:
                return null;
        }
    }

}
