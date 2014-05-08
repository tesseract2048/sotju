package org.tju.so.crawler.service;

import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.service.JedisService;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class Storage {

    private static final Logger LOG = LoggerFactory.getLogger(Storage.class);

    @Autowired
    private JedisService jedisService;

    @Resource
    private String queueKeyFormat;

    @Resource
    private String contextKey;

    @Resource
    private String refreshKey;

    @Resource
    private String contextReferenceKey;

    public void putContext(final Context context) throws Exception {
        final String document = new Gson().toJson(context);
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                jedis.zincrby(contextReferenceKey, 0, context.getContextId());
                return jedis.hset(contextKey, context.getContextId(), document);
            }

        });
        LOG.info("Context " + context.getContextId() + " updated.");
    }

    public void removeContext(final String contextId) throws Exception {
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                jedis.zrem(contextReferenceKey, contextId);
                return jedis.hdel(contextKey, contextId);
            }

        });
    }

    public Context getContext(final String contextId) throws Exception {
        return jedisService.commit(new JedisTransaction<Context>() {

            @Override
            public Context execute(Jedis jedis) throws Exception {
                String document = jedis.hget(contextKey, contextId);
                return new Gson().fromJson(document, Context.class);
            }

        });
    }

    public void putRefresh(final String url, final long time) throws Exception {
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                return jedis.hset(refreshKey, url, String.valueOf(time));
            }

        });
        LOG.debug("Refresh time for " + url + " updated to " + time + ".");
    }

    public long getRefresh(final String url) throws Exception {
        Long time = jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                String document = jedis.hget(refreshKey, url);
                if (document == null || document.equals("nil"))
                    return null;
                return Long.valueOf(document);
            }

        });
        if (time == null)
            return 0;
        return time;
    }

    public Task popTask(final TaskPriority priority) throws Exception {
        return jedisService.commit(new JedisTransaction<Task>() {

            @Override
            public Task execute(Jedis jedis) throws Exception {
                String queueKey = String.format(queueKeyFormat,
                        priority.toString());
                String document = jedis.lpop(queueKey);
                if (document == null || document.equals("nil"))
                    return null;
                return new Gson().fromJson(document, Task.class);
            }

        });

    }

    public void finishTask(final Task task) throws Exception {
        jedisService.commit(new JedisTransaction<Double>() {

            @Override
            public Double execute(Jedis jedis) throws Exception {
                return jedis.zincrby(contextReferenceKey, -1,
                        task.getContextId());
            }

        });
    }

    public void pushTask(final Task task) throws Exception {
        final String document = new Gson().toJson(task);
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                String queueKey = String.format(queueKeyFormat, task
                        .getPriority().toString());
                jedis.zincrby(contextReferenceKey, 1, task.getContextId());
                return jedis.lpush(queueKey, document);
            }

        });
        LOG.debug("New task created: " + task.toString());
    }

    public long currentTime() {
        return System.currentTimeMillis() / 1000;
    }

    public int contextGC() throws Exception {
        return jedisService.commit(new JedisTransaction<Integer>() {

            @Override
            public Integer execute(Jedis jedis) throws Exception {
                Set<String> collecting = jedis.zrangeByScore(
                        contextReferenceKey, 0, 0);
                for (String key: collecting) {
                    removeContext(key);
                }
                return collecting.size();
            }

        });
    }
}
