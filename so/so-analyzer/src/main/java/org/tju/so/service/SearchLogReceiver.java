package org.tju.so.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.handler.LogHandler;
import org.tju.so.search.log.EntryType;
import org.tju.so.search.log.LogEntry;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

/**
 * Service to provide search logging server behaviors
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class SearchLogReceiver implements DisposableBean {

    private static final Logger LOG = LoggerFactory
            .getLogger(SearchLogReceiver.class);

    @Autowired
    private JedisService jedisService;

    @Resource
    private String logKey;

    @Resource
    private List<LogHandler> logHandlers;

    private volatile boolean isRunning;

    private class JedisFetch implements JedisTransaction<LogEntry> {

        @SuppressWarnings("unchecked")
        @Override
        public LogEntry execute(Jedis jedis) throws Exception {
            String message = jedis.lpop(logKey);
            if (message == null || message.equals("nil")) {
                return null;
            }
            int pos = message.indexOf(":");
            EntryType type = EntryType.valueOf(message.substring(0, pos));
            String entryJson = message.substring(pos + 1);
            return (LogEntry) new Gson().fromJson(entryJson, type.getClazz());
        }

    }

    public void run() throws Exception {
        isRunning = true;
        LOG.info("Search log receiver started.");
        while (isRunning) {
            LogEntry entry = jedisService.commit(new JedisFetch());
            if (entry != null) {
                handle(entry);
                Thread.sleep(16);
            } else {
                Thread.sleep(100);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        isRunning = false;
    }

    public void handle(LogEntry entry) {
        for (LogHandler handler: logHandlers) {
            handler.handle(entry);
        }
    }

}
