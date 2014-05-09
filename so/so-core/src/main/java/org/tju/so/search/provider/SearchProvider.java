package org.tju.so.search.provider;

import java.util.List;

import org.tju.so.model.entity.Entity;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;

/**
 * Provides searching related behaviors, including index, query, etc.
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface SearchProvider {

    /**
     * Execute specified query on search engine and build its context
     * 
     * @param query
     * @return A context contains query, results and other important meta
     */
    Context search(Query query);

    /**
     * Store and build index for one or several entities on search engine
     * 
     * @param entities
     * @return
     */
    boolean index(Entity... entities);

    /**
     * Remove entity and its index from search engine
     * 
     * @param entity
     * @return
     */
    boolean delete(Entity entity);

    /**
     * Create or update index container for specified site on search engine
     * 
     * @param site
     * @return
     */
    boolean updateSite(Site site);

    /**
     * Create or update specified schema across specified sites on search engine
     * 
     * @param sites
     * @param schema
     * @return
     */
    boolean updateSchema(Site[] sites, Schema schema);

    /**
     * Run first-time initialization on search engine
     * 
     * @return
     */
    boolean initIndices();

    /**
     * Get auto-completions for specified keyword
     * 
     * @param keyword
     * @param limit
     * @return
     */
    List<String> getCompletions(String keyword, int limit);
}
