package org.tju.so.handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.tju.so.search.log.LogEntry;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class BinaryLogWriterHandler implements LogHandler, DisposableBean {

    private static final Logger LOG = LoggerFactory
            .getLogger(BinaryLogWriterHandler.class);

    @Resource
    private String binlogFile;

    private ObjectOutputStream oos;

    @PostConstruct
    public void init() throws Exception {
        oos = new ObjectOutputStream(new FileOutputStream(binlogFile, true));
        LOG.info("Binary log writer handler initialized.");
    }

    @Override
    public void destroy() throws Exception {
        if (oos != null)
            oos.close();
    }

    @Override
    public void handle(LogEntry entry) {
        try {
            oos.writeObject(entry);
        } catch (IOException e) {
            LOG.error("Failed to write binlog", e);
        }
    }

}
