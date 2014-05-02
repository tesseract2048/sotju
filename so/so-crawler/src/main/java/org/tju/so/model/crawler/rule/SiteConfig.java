package org.tju.so.model.crawler.rule;

import java.util.List;

import org.tju.so.model.IdBasedObject;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class SiteConfig implements IdBasedObject {

    private String id;

    private List<Seed> seeds;

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
     * @return the seeds
     */
    public List<Seed> getSeeds() {
        return seeds;
    }

    /**
     * @param seeds
     *            the seeds to set
     */
    public void setSeeds(List<Seed> seeds) {
        this.seeds = seeds;
    }

}
