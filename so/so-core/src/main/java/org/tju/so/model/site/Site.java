package org.tju.so.model.site;

import org.tju.so.model.IdBasedObject;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Site implements IdBasedObject {

    private String id;

    private String name;

    private String homeUrl;

    private float rankingFactors;

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the homeUrl
     */
    public String getHomeUrl() {
        return homeUrl;
    }

    /**
     * @param homeUrl
     *            the homeUrl to set
     */
    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    /**
     * @return the rankingFactors
     */
    public float getRankingFactors() {
        return rankingFactors;
    }

    /**
     * @param rankingFactors
     *            the rankingFactors to set
     */
    public void setRankingFactors(float rankingFactors) {
        this.rankingFactors = rankingFactors;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Site [id=" + id + ", name=" + name + ", homeUrl=" + homeUrl
                + ", rankingFactors=" + rankingFactors + "]";
    }

}
