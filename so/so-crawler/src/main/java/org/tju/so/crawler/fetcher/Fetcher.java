package org.tju.so.crawler.fetcher;

import java.io.IOException;
import java.util.Map;

/**
 * Provides fetching behavior for crawler
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Fetcher {

    /**
     * Initialize fetcher on specified url
     * 
     * @param url
     */
    void init(String url);

    /**
     * Initialize fetcher on specified url with specified method and post data
     * 
     * @param method
     * @param url
     * @param postData
     */
    void init(String method, String url, String postData);

    /**
     * Initialize fetcher on specified url with specified method, post content
     * type and post data
     * 
     * @param method
     * @param url
     * @param postDataType
     * @param postData
     */
    void init(String method, String url, String postDataType, String postData);

    /**
     * Send request with specified headers and retrieve response
     * 
     * @param requestHeaders
     * @throws IOException
     */
    void fetch(Map<String, String> requestHeaders) throws IOException;

    /**
     * Get body of retrieved response
     * 
     * @return
     */
    byte[] getData();

    /**
     * Get mime type of body of retrieved response
     * 
     * @return
     */
    String getMimeType();

    /**
     * Get header with specified key in retrieved response
     * 
     * @param key
     * @return
     */
    String getResponseHeader(String key);
}
