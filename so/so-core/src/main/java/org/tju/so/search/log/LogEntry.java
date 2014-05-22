package org.tju.so.search.log;

import java.io.Serializable;

/**
 * Description for individual log entry
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface LogEntry extends Serializable {

    public EntryType getType();
}
