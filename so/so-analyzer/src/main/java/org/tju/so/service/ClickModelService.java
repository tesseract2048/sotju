package org.tju.so.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.QueryItemCounter;
import org.tju.so.model.QueryItemIdentity;
import org.tju.so.model.QueryPositionCounter;
import org.tju.so.model.QuerySession;
import org.tju.so.model.QuerySession.ClickItem;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class ClickModelService {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClickModelService.class);

    @Resource
    private int dcmMaxPosition;

    @Resource
    private String dcmQueryCounterKey;

    @Resource
    private String dcmPositionCounterKey;

    @Autowired
    private JedisService jedisService;

    public List<QuerySession> getSessions() {
        // TODO: dig sessions in binlog
        return null;
    }

    private void putItemCounters(final String keyword,
            final Map<QueryItemIdentity, QueryItemCounter> counters)
            throws Exception {
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                String key = String.format(dcmQueryCounterKey, keyword);
                jedis.del(key);
                for (Map.Entry<QueryItemIdentity, QueryItemCounter> entry: counters
                        .entrySet()) {
                    jedis.hset(key, entry.getKey().toString(), entry.getValue()
                            .toString());
                }
                return 0L;
            }

        });
    }

    private Map<QueryItemIdentity, QueryItemCounter> getItemCounters(
            final String keyword) throws Exception {
        return jedisService
                .commit(new JedisTransaction<Map<QueryItemIdentity, QueryItemCounter>>() {

                    @Override
                    public Map<QueryItemIdentity, QueryItemCounter> execute(
                            Jedis jedis) throws Exception {
                        String key = String.format(dcmQueryCounterKey, keyword);
                        Map<QueryItemIdentity, QueryItemCounter> counters = new HashMap<QueryItemIdentity, QueryItemCounter>();
                        Map<String, String> entries = jedis.hgetAll(key);
                        if (entries == null)
                            return new HashMap<QueryItemIdentity, QueryItemCounter>();
                        for (Map.Entry<String, String> entry: entries
                                .entrySet()) {
                            QueryItemIdentity identity = QueryItemIdentity
                                    .valueOf(entry.getKey());
                            QueryItemCounter counter = QueryItemCounter
                                    .valueOf(entry.getValue());
                            counters.put(identity, counter);
                        }
                        return counters;
                    }

                });
    }

    private void putPositionCounters(final List<QueryPositionCounter> counters)
            throws Exception {
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                for (int i = 0; i < counters.size(); i++) {
                    jedis.lset(dcmPositionCounterKey, i, counters.get(i)
                            .toString());
                }
                return 0L;
            }

        });
    }

    private List<QueryPositionCounter> getPositionCounters(final int count)
            throws Exception {
        return jedisService
                .commit(new JedisTransaction<List<QueryPositionCounter>>() {

                    @Override
                    public List<QueryPositionCounter> execute(Jedis jedis)
                            throws Exception {
                        List<QueryPositionCounter> counters = new ArrayList<QueryPositionCounter>();
                        List<String> items = jedis.lrange(
                                dcmPositionCounterKey, 0, count);
                        if (items == null)
                            return new ArrayList<QueryPositionCounter>();
                        for (String item: items)
                            counters.add(QueryPositionCounter.valueOf(item));
                        return counters;
                    }

                });
    }

    /**
     * update dcm counters with specified sessions
     * 
     * @param sessions
     *            Clicks must be in ascending order and distincted
     * @throws Exception
     */
    public void update(List<QuerySession> sessions) throws Exception {
        List<QueryPositionCounter> posCounters = getPositionCounters(dcmMaxPosition);
        while (posCounters.size() < dcmMaxPosition)
            posCounters.add(new QueryPositionCounter());
        for (QuerySession session: sessions) {
            Map<QueryItemIdentity, QueryItemCounter> counters = getItemCounters(session
                    .getKeyword());
            LOG.info("Updating counters for keyword [" + session.getKeyword()
                    + "]");
            for (int i = 0; i < session.getClicks().size(); i++) {
                ClickItem item = session.getClicks().get(i);

                /* update item counter */
                QueryItemCounter counter = counters.get(item);
                if (counter == null) {
                    counter = new QueryItemCounter();
                    counters.put(item, counter);
                }
                counter.setPosition(item.getPosition());
                counter.incrClickBy(1);
                if (i < session.getClicks().size() - 1)
                    counter.incrImpressionBy(1);

                /* update position counter */
                if (item.getPosition() >= dcmMaxPosition)
                    continue;
                QueryPositionCounter posCounter = posCounters.get(item
                        .getPosition());
                posCounter.incrClickBy(1);
                if (i < session.getClicks().size() - 1)
                    posCounter.incrLastClickBy(1);
            }
            putItemCounters(session.getKeyword(), counters);
        }
        putPositionCounters(posCounters);
    }
}
