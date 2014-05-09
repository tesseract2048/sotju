package org.tju.so.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Service to provide jedis transactions
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class JedisService {

    public static interface JedisTransaction<T> {
        T execute(Jedis jedis) throws Exception;
    }

    @Resource
    private JedisPool jedisPool;

    /**
     * Commit a jedis transaction after which the client resource could be
     * reused
     * 
     * @param transaction
     * @return
     * @throws Exception
     */
    public <T> T commit(JedisTransaction<T> transaction) throws Exception {
        return commit(transaction, true);
    }

    /**
     * Commit a jedis transaction while indicates whether the client resource
     * could be reused
     * 
     * @param transaction
     * @param returnClient
     * @return
     * @throws Exception
     */
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
