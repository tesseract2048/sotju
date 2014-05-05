package org.tju.so.crawler.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class Storage {

    private static final Logger LOG = LoggerFactory.getLogger(Storage.class);

    private static interface JedisTransaction<T> {
        T execute(Jedis jedis) throws Exception;
    }

    @Resource
    private JedisPool jedisPool;

    @Resource
    private String queueKeyFormat;

    @Resource
    private String contextKey;

    @Resource
    private String refreshKey;

    private <T> T commit(JedisTransaction<T> transaction) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return transaction.execute(jedis);
        } catch (Exception e) {
            throw e;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void putContext(final Context context) throws Exception {
        final String document = new Gson().toJson(context);
        commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                return jedis.hset(contextKey, context.getContextId(), document);
            }

        });
        LOG.info("Context " + context.getContextId() + " updated.");
    }

    public void removeContext(final String contextId) throws Exception {
        commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                return jedis.hdel(contextKey, contextId);
            }

        });
    }

    public Context getContext(final String contextId) throws Exception {
        return commit(new JedisTransaction<Context>() {

            @Override
            public Context execute(Jedis jedis) throws Exception {
                String document = jedis.hget(contextKey, contextId);
                return new Gson().fromJson(document, Context.class);
            }

        });
    }

    public void putRefresh(final String url, final long time) throws Exception {
        commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                return jedis.hset(refreshKey, url, String.valueOf(time));
            }

        });
        LOG.debug("Refresh time for " + url + " updated to " + time + ".");
    }

    public long getRefresh(final String url) throws Exception {
        Long time = commit(new JedisTransaction<Long>() {

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
        return commit(new JedisTransaction<Task>() {

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

    public void pushTask(final Task task) throws Exception {
        final String document = new Gson().toJson(task);
        commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                String queueKey = String.format(queueKeyFormat, task
                        .getPriority().toString());
                return jedis.lpush(queueKey, document);
            }

        });
        LOG.debug("New task created: " + task.toString());
    }

    public long currentTime() {
        return System.currentTimeMillis() / 1000;
    }
}
