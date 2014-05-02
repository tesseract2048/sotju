package org.tju.so.model.crawler.data;

import org.tju.so.model.crawler.TaskPriority;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Task {

    private String contextId;

    private String url;

    private TaskPriority priority;

    public Task() {}

    public Task(String contextId, String url, TaskPriority priority) {
        setContextId(contextId);
        setUrl(url);
        setPriority(priority);
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
        return "Task [contextId=" + contextId + ", url=" + url + ", priority="
                + priority + "]";
    }

}
