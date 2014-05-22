package org.tju.so.handler;

import org.tju.so.search.log.LogEntry;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public interface LogHandler {

    /* handle a single log entry */
    void handle(LogEntry entry);
}
