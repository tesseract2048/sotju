package org.tju.so.handler;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.search.log.EntryType;
import org.tju.so.search.log.LogEntry;
import org.tju.so.search.log.SearchLogEntry;
import org.tju.so.service.JedisService;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class HotwordHandler implements LogHandler {

    private static final long HOT_FREQ_THRESHOLD = 10;

    private static final double HOT_WEIGHT_RATE_FACTOR = 0.7;

    private static final Logger LOG = LoggerFactory
            .getLogger(HotwordHandler.class);

    @Resource
    private String queryFreqKey;

    @Resource
    private String queryHotwordKey;

    @Autowired
    private JedisService jedisService;

    private int getDateId() {
        return (int) (new Date().getTime() / 1000 / 86400);
    }

    private String getFreqKey(int offset) {
        return String.format(queryFreqKey, getDateId() + offset);
    }

    private String normalizeKeyword(String keyword) {
        return keyword;
    }

    @Override
    public void handle(LogEntry entry) {
        if (entry.getType() == EntryType.SEARCH) {
            SearchLogEntry searchLogEntry = (SearchLogEntry) entry;
            final String normalizedKeyword = normalizeKeyword(searchLogEntry
                    .getQuery().getQuery());
            if (StringUtils.isEmpty(normalizedKeyword))
                return;
            try {
                jedisService.commit(new JedisTransaction<Double>() {

                    @Override
                    public Double execute(Jedis jedis) throws Exception {
                        long freq = jedis.hincrBy(getFreqKey(0),
                                normalizedKeyword, 1);
                        String lastFreqStr = jedis.hget(getFreqKey(-1),
                                normalizedKeyword);
                        if (StringUtils.isEmpty(lastFreqStr)
                                || lastFreqStr.equals("nil"))
                            return 0.0d;
                        long lastFreq = Long.valueOf(lastFreqStr);
                        if (freq < HOT_FREQ_THRESHOLD)
                            return 0.0d;
                        double growthRate = (double) (freq - lastFreq)
                                / lastFreq;
                        double weight = growthRate * HOT_WEIGHT_RATE_FACTOR
                                + Math.log(freq) * (1 - HOT_WEIGHT_RATE_FACTOR);
                        jedis.zadd(queryHotwordKey, weight, normalizedKeyword);
                        return weight;
                    }

                });
            } catch (Exception e) {
                LOG.error("failed to handle search log", e);
            }
        }
    }

}
