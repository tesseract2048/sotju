package org.tju.so.crawler.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class HttpFetcher implements Fetcher {

    private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36 (Sotju/Fetcher)";

    private final static int TIMEOUT = 30000;

    private String url;

    private Map<String, String> responseHeaders;

    private byte[] content;

    @Override
    public void init(String url) {
        this.url = url;
    }

    @Override
    public void fetch(Map<String, String> requestHeaders) throws IOException {
        URL target = new URL(url);
        URLConnection connection = target.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);
        for (Map.Entry<String, String> entry: requestHeaders.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        InputStream input = connection.getInputStream();
        try {
            content = IOUtils.toByteArray(input);
        } finally {
            IOUtils.closeQuietly(input);
        }
        responseHeaders = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> entry: connection
                .getHeaderFields().entrySet()) {
            String value = null;
            for (String item: entry.getValue()) {
                if (value == null)
                    value = item;
                else
                    value += "," + item;
            }
            responseHeaders.put(entry.getKey(), value);
        }
    }

    @Override
    public byte[] getData() {
        return content;
    }

    @Override
    public String getResponseHeader(String key) {
        return responseHeaders.get(key);
    }

    @Override
    public String getMimeType() {
        return responseHeaders.get("Content-Type");
    }

}
