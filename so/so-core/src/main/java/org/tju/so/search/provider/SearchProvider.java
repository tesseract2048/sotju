package org.tju.so.search.provider;

import org.tju.so.model.entity.Entity;
import org.tju.so.model.schema.Schema;
import org.tju.so.model.site.Site;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface SearchProvider {

    Context search(Query query);

    boolean index(Entity... entities);

    boolean delete(Entity entity);

    boolean updateSite(Site site);

    boolean updateSchema(Site[] sites, Schema schema);

}
