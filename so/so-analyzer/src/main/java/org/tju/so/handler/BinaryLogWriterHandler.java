package org.tju.so.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.search.log.LogEntry;
import org.tju.so.service.BinlogService;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class BinaryLogWriterHandler implements LogHandler {

    @Autowired
    private BinlogService binlogService;

    @Override
    public void handle(LogEntry entry) {
        binlogService.write(entry);
    }

}
