package org.tju.so.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public class LogReader {
        private ObjectInputStream ois = null;

        public LogReader(String fileName) throws IOException {
            ois = new ObjectInputStream(new FileInputStream(fileName));
        }

        public LogEntry read() {
            try {
                return (LogEntry) ois.readObject();
            } catch (Exception e) {
                return null;
            }
        }

        public List<LogEntry> read(int count) {
            List<LogEntry> entries = new ArrayList<LogEntry>();
            for (int i = 0; i < count; i++) {
                LogEntry entry = read();
                if (entry == null)
                    break;
                entries.add(entry);
            }
            return entries;
        }

        public List<LogEntry> readToEnd() {
            List<LogEntry> entries = new ArrayList<LogEntry>();
            while (true) {
                LogEntry entry = read();
                if (entry == null)
                    break;
                entries.add(entry);
            }
            return entries;
        }

        public void close() {
            try {
                ois.close();
            } catch (IOException e) {}
        }
    }

    private static final Logger LOG = LoggerFactory
            .getLogger(BinlogService.class);

    @Resource
    private String binlogFile;

    private String rollingFile;

    private Date rollingDate;

    private ObjectOutputStream oos = null;

    @Override
    public void destroy() throws Exception {
        if (oos != null)
            oos.close();
    }

    private String getFileName(Date date) {
        return String.format(binlogFile,
                new SimpleDateFormat("YYYY-mm-dd").format(date));
    }

    private void resetDate() {
        rollingDate = getCurrentDate();
        rollingFile = getFileName(rollingDate);
    }

    private Date getCurrentDate() {
        return new Date((new Date()).getTime() / 86400 * 86400);
    }

    private void rollLog() throws IOException {
        if (getCurrentDate().equals(rollingDate))
            return;
        if (oos != null)
            oos.close();
        resetDate();
        oos = new ObjectOutputStream(new FileOutputStream(rollingFile, true));
    }

    public synchronized void write(LogEntry entry) {
        try {
            rollLog();
            oos.writeObject(entry);
        } catch (IOException e) {
            LOG.error("Failed to write binlog", e);
        }
    }

    public LogReader createLogReader(String date) throws IOException {
        return new LogReader(String.format(binlogFile, date));
    }

    public LogReader createLogReader(Date date) throws IOException {
        return new LogReader(getFileName(date));
    }

}
