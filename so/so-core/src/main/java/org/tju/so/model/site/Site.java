package org.tju.so.model.site;

import org.tju.so.model.IdBasedObject;
import org.tju.so.model.schema.Schema;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface Site extends IdBasedObject {

    String getId();

    String getName();

    String getHomeUrl();

    float getRankingFactor(Schema schema);

}
