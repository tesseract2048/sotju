package org.tju.so.crawler.parser;

import java.util.List;
import java.util.Map;

import org.tju.so.model.crawler.rule.Extractor;

/**
 * Provides parser behavior for crawler
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Parser {

    /**
     * Initialize parser on specified data with specified mime type
     * 
     * @param mimeType
     * @param data
     */
    void init(String mimeType, byte[] data);

    /**
     * Extract matches with specified extractor
     * 
     * @param extractor
     * @return
     */
    List<Map<String, Object>> extract(Extractor extractor);
}
