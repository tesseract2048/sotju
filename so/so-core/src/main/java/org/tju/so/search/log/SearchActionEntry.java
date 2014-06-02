package org.tju.so.search.log;

import java.io.Serializable;
import java.util.Date;

import org.tju.so.search.context.Query;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface SearchActionEntry extends Serializable {

    public Date getDate();
    
    public String getUserIdentifier();
    
    public Query getQuery();
}
