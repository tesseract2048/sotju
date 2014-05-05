package org.tju.so.crawler.fetcher;

import java.io.IOException;
import java.util.Map;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Fetcher {

    void init(String url);

    void init(String method, String url, String postData);

    void init(String method, String url, String postDataType, String postData);

    void fetch(Map<String, String> requestHeaders) throws IOException;

    byte[] getData();

    String getMimeType();

    String getResponseHeader(String key);
}
