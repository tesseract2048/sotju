package org.tju.so.search.result;

import java.util.List;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Response {

    private List<Item> items;

    private float queryTime;

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @param items
     *            the items to set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * @return the queryTime
     */
    public float getQueryTime() {
        return queryTime;
    }

    /**
     * @param queryTime
     *            the queryTime to set
     */
    public void setQueryTime(float queryTime) {
        this.queryTime = queryTime;
    }

}
