package org.tju.so.model.crawler.rule;

import java.util.HashMap;
import java.util.Map;

import org.tju.so.model.crawler.TaskPriority;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Seed {

    private String url;

    private Map<String, Object> params;

    private int frequency;

    private TaskPriority priority;

    public Seed() {
        params = new HashMap<String, Object>();
    };

    public Seed(String url, int frequency, TaskPriority priority) {
        this();
        setUrl(url);
        setFrequency(frequency);
        setPriority(priority);
    }

    public Seed(String url, int frequency, TaskPriority priority,
            Map<String, Object> params) {
        this(url, frequency, priority);
        setParams(params);
    }

    public Seed(String url, int frequency, TaskPriority priority,
            String postData) {
        this(url, frequency, priority);
        params.put("postData", postData);
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param frequency
     *            the frequency to set
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the priority
     */
    public TaskPriority getPriority() {
        return priority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @param params
     *            the params to set
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Seed [url=" + url + ", params=" + params + ", frequency="
                + frequency + ", priority=" + priority + "]";
    }
}
