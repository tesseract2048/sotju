package org.tju.so.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.DocumentIdentity;
import org.tju.so.model.QueryItemCounter;
import org.tju.so.model.QueryItemIdentity;
import org.tju.so.model.QueryPositionCounter;
import org.tju.so.model.QuerySession;
import org.tju.so.model.QuerySession.ClickItem;
import org.tju.so.search.context.Query;
import org.tju.so.search.log.ClickLogEntry;
import org.tju.so.search.log.EntryType;
import org.tju.so.search.log.LogEntry;
import org.tju.so.search.log.SearchActionEntry;
import org.tju.so.search.log.SearchLogEntry;
import org.tju.so.service.BinlogService.LogReader;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

/**
 * Dependency Click Model. Reference:
 * http://research.microsoft.com/pubs/73115/multiple-click-model-wsdm09.pdf
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class ClickModelService {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClickModelService.class);

    @Resource
    private int dcmMaxPosition;

    @Resource
    private long dcmMaxSessionTime;

    @Resource
    private String dcmQueryCounterKey;

    @Resource
    private String dcmPositionCounterKey;

    @Autowired
    private JedisService jedisService;

    @Autowired
    private BinlogService binlogService;

    private static class SessionIdentity {
        public String u;

        public Query q;

        public SessionIdentity(LogEntry e) {
            SearchActionEntry se = (SearchActionEntry) e;
            q = se.getQuery();
            u = se.getUserIdentifier();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SessionIdentity))
                return false;
            SessionIdentity s = (SessionIdentity) obj;
            if (!s.u.equals(u))
                return false;
            if (!s.q.equals(q))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            return u.hashCode() + q.hashCode();
        }
    }

    private void processLogEntry(
            Map<SessionIdentity, List<QuerySession>> sessions, LogEntry entry) {
        if (entry.getType() != EntryType.SEARCH
                && entry.getType() != EntryType.CLICK)
            return;

        /*
         * get session list for particular <userIdentity, query> pair
         */
        SessionIdentity sessionId = new SessionIdentity(entry);
        if (!sessions.containsKey(sessionId))
            sessions.put(sessionId, new ArrayList<QuerySession>());
        List<QuerySession> qSessions = sessions.get(sessionId);

        Date actionDate = ((SearchActionEntry) entry).getDate();
        QuerySession matchSession = null;
        for (QuerySession session: qSessions) {
            if (Math.abs(actionDate.getTime()
                    - session.getStartDate().getTime()) < dcmMaxSessionTime) {
                matchSession = session;
                break;
            }
        }

        if (entry.getType() == EntryType.SEARCH && matchSession == null) {
            SearchLogEntry searchLogEntry = (SearchLogEntry) entry;
            QuerySession session = new QuerySession();
            session.setStartDate(searchLogEntry.getDate());
            session.setKeyword(searchLogEntry.getQuery().getQuery());
            session.setClicks(new ArrayList<ClickItem>());
            qSessions.add(session);
        }

        if (entry.getType() == EntryType.CLICK && matchSession != null) {
            ClickLogEntry clickLogEntry = (ClickLogEntry) entry;
            ClickItem clickItem = new ClickItem(clickLogEntry);
            List<ClickItem> clickItems = matchSession.getClicks();
            if (clickItems.size() > 0
                    && clickItems.get(clickItems.size() - 1).getPosition() >= clickItem
                            .getPosition())
                return;
            clickItems.add(clickItem);
        }
    }

    private List<QuerySession> getSessions(LogReader reader) throws IOException {
        Map<SessionIdentity, List<QuerySession>> sessions = new HashMap<SessionIdentity, List<QuerySession>>();
        while (true) {
            List<LogEntry> entries = reader.read(1000);
            if (entries.size() == 0)
                break;
            for (LogEntry entry: entries)
                processLogEntry(sessions, entry);
        }
        List<QuerySession> lstSession = new ArrayList<QuerySession>();
        for (Map.Entry<SessionIdentity, List<QuerySession>> entry: sessions
                .entrySet()) {
            for (QuerySession session: entry.getValue()) {
                lstSession.add(session);
            }
        }
        return lstSession;
    }

    public List<QuerySession> getSessions(Date date) throws IOException {
        LogReader reader = null;
        try {
            reader = binlogService.createLogReader(date);
            return getSessions(reader);
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public List<QuerySession> getSessions(String date) throws IOException {
        LogReader reader = null;
        try {
            reader = binlogService.createLogReader(date);
            return getSessions(reader);
        } finally {
            if (reader != null)
                reader.close();
        }
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

    public void reset() throws Exception {
        jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                Set<String> keys = jedis.keys(String.format(dcmQueryCounterKey,
                        "*"));
                for (String key: keys) {
                    jedis.del(key);
                }
                jedis.del(dcmPositionCounterKey);
                return 0L;
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

    public List<Double> getPositionBias(int count) throws Exception {
        List<QueryPositionCounter> posCounters = getPositionCounters(count);
        while (posCounters.size() < count)
            posCounters.add(new QueryPositionCounter());
        List<Double> bias = new ArrayList<Double>();
        for (QueryPositionCounter counter: posCounters) {
            bias.add(1 - ((double) counter.getClick() / (double) counter
                    .getLastClick()));
        }
        return bias;
    }

    public Map<DocumentIdentity, Double> getRelevance(String keyword)
            throws Exception {
        Map<QueryItemIdentity, QueryItemCounter> counters = getItemCounters(keyword);
        Map<DocumentIdentity, Double> relevances = new HashMap<DocumentIdentity, Double>();
        for (Map.Entry<QueryItemIdentity, QueryItemCounter> counterEntry: counters
                .entrySet()) {
            QueryItemCounter counter = counterEntry.getValue();
            double relevance;
            if (counter.getClick() == 0) {
                relevance = 0.0d;
            } else if (counter.getImpression() == 0) {
                relevance = Double.POSITIVE_INFINITY;
            } else {
                relevance = (double) counter.getClick()
                        / (double) counter.getImpression();
            }
            relevances.put(counterEntry.getKey(), relevance);
        }
        return relevances;
    }

    public int getMaxPosition() {
        return dcmMaxPosition;
    }
}
