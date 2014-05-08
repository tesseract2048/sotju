package org.tju.so.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class JedisService {

    public static interface JedisTransaction<T> {
        T execute(Jedis jedis) throws Exception;
    }

    @Resource
    private JedisPool jedisPool;

    public <T> T commit(JedisTransaction<T> transaction) throws Exception {
        return commit(transaction, true);
    }

    public <T> T commit(JedisTransaction<T> transaction, boolean returnClient)
            throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return transaction.execute(jedis);
        } catch (Exception e) {
            throw e;
        } finally {
            if (jedis != null && returnClient) {
                jedisPool.returnResource(jedis);
            }
        }
    }

}
