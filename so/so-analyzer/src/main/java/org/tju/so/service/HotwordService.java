package org.tju.so.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class HotwordService {

    private static final Logger LOG = LoggerFactory
            .getLogger(HotwordService.class);

    @Resource
    private String queryHotwordKey;

    @Autowired
    private JedisService jedisService;

    public List<String> getHotwords(final int limit) {
        try {
            return jedisService.commit(new JedisTransaction<List<String>>() {

                @Override
                public List<String> execute(Jedis jedis) throws Exception {
                    Set<String> keywords = jedis.zrange(queryHotwordKey, 0,
                            limit);
                    return new ArrayList<String>(keywords);
                }

            });
        } catch (Exception e) {
            LOG.error("failed to get hotword", e);
            return null;
        }
    }
}
