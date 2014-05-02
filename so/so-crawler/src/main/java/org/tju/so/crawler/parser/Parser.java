package org.tju.so.crawler.parser;

import java.util.List;
import java.util.Map;

import org.tju.so.model.crawler.rule.Extractor;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Parser {

    void init(String mimeType, byte[] data);

    List<Map<String, Object>> extract(Extractor extractor);
}
