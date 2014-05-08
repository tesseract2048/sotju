package org.tju.so.model.crawler.data;

import java.util.HashMap;
import java.util.Map;

import org.tju.so.model.crawler.TaskPriority;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Task {

    private String contextId;

    private String url;

    private Map<String, Object> params;

    private TaskPriority priority;

    public Task() {
        params = new HashMap<String, Object>();
    }

    public Task(String contextId, String url, TaskPriority priority) {
        this();
        setContextId(contextId);
        setUrl(url);
        setPriority(priority);
    }

    public Task(String contextId, String url, Map<String, Object> params,
            TaskPriority priority) {
        this(contextId, url, priority);
        setParams(params);
    }

    /**
     * @return the contextId
     */
    public String getContextId() {
        return contextId;
    }

    /**
     * @param contextId
     *            the contextId to set
     */
    public void setContextId(String contextId) {
        this.contextId = contextId;
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
        if (params == null) return;
        this.params = params;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Task [contextId=" + contextId + ", url=" + url + ", params="
                + params + ", priority=" + priority + "]";
    }

}
