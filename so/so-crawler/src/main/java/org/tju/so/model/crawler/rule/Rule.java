package org.tju.so.model.crawler.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tju.so.model.IdBasedObject;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Rule implements IdBasedObject {

    public final static int NEVER_REFRESH = -1;

    public final static int EVER_REFRESH = 0;

    private String id;

    private String siteId;

    private String urlPattern;

    private int refreshRate;

    private Map<String, String> headers;

    private List<Extractor> extractors;

    public Rule() {
        headers = new HashMap<String, String>();
        extractors = new ArrayList<Extractor>();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * @param siteId
     *            the siteId to set
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * @return the urlPattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * @param urlPattern
     *            the urlPattern to set
     */
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * @return the refreshRate
     */
    public int getRefreshRate() {
        return refreshRate;
    }

    /**
     * @param refreshRate
     *            the refreshRate to set
     */
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers
     *            the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the extractors
     */
    public List<Extractor> getExtractors() {
        return extractors;
    }

    /**
     * @param extractors
     *            the extractors to set
     */
    public void setExtractors(List<Extractor> extractors) {
        this.extractors = extractors;
    }

}
