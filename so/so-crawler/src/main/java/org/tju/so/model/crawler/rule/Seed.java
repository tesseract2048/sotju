package org.tju.so.model.crawler.rule;

import org.tju.so.model.crawler.TaskPriority;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Seed {

    private String url;

    private int frequency;

    private TaskPriority priority;

    public Seed() {};

    public Seed(String url, int frequency, TaskPriority priority) {
        setUrl(url);
        setFrequency(frequency);
        setPriority(priority);
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
}
