package org.tju.so.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.tju.so.search.log.LogEntry;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class BinlogService implements DisposableBean {

    private static final Logger LOG = LoggerFactory
            .getLogger(BinlogService.class);

    @Resource
    private String binlogFile;

    private ObjectOutputStream oos = null;

    @PostConstruct
    public void init() throws Exception {
        LOG.info("Binary log service initialized.");
    }

    @Override
    public void destroy() throws Exception {
        if (oos != null)
            oos.close();
    }

    public synchronized void write(LogEntry entry) {
        try {
            if (oos == null)
                oos = new ObjectOutputStream(new FileOutputStream(binlogFile,
                        true));
            oos.writeObject(entry);
        } catch (IOException e) {
            LOG.error("Failed to write binlog", e);
        }
    }

}
