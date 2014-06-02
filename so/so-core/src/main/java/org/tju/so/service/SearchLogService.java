package org.tju.so.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;
import org.tju.so.search.context.ResultItem;
import org.tju.so.search.log.ClickLogEntry;
import org.tju.so.search.log.CompleteLogEntry;
import org.tju.so.search.log.LogEntry;
import org.tju.so.search.log.SearchLogEntry;
import org.tju.so.search.log.SearchResultItemLogEntry;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

/**
 * Service to provide search logging client behaviors
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class SearchLogService {

    @Autowired
    private JedisService jedisService;

    @Resource
    private String logKey;

    public void write(final LogEntry entry) throws Exception {
        jedisService.commit(new JedisTransaction<Long>() {
            @Override
            public Long execute(Jedis jedis) throws Exception {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getType().toString());
                sb.append(":");
                sb.append(new Gson().toJson(entry));
                return jedis.rpush(logKey, sb.toString());
            }
        });
    }

    public void writeComplete(String userIdentifier, String keyword, int limit,
            List<String> completions) throws Exception {
        CompleteLogEntry completeLogEntry = new CompleteLogEntry();
        completeLogEntry.setUserIdentifier(userIdentifier);
        completeLogEntry.setKeyword(keyword);
        completeLogEntry.setLimit(limit);
        completeLogEntry.setCompletions(completions);
        completeLogEntry.setDate(new Date());
        write(completeLogEntry);
    }

    public void writeClick(String userIdentifier, Query query, String schemaId,
            String siteId, String id, int position) throws Exception {
        ClickLogEntry clickLogEntry = new ClickLogEntry();
        clickLogEntry.setUserIdentifier(userIdentifier);
        clickLogEntry.setQuery(query);
        clickLogEntry.setSchemaId(schemaId);
        clickLogEntry.setSiteId(siteId);
        clickLogEntry.setId(id);
        clickLogEntry.setPosition(position);
        clickLogEntry.setDate(new Date());
        write(clickLogEntry);
    }

    public void writeSearch(Context context) throws Exception {
        SearchLogEntry searchLogEntry = new SearchLogEntry();
        searchLogEntry.setUserIdentifier(context.getUserIdentifier());
        searchLogEntry.setQuery(context.getQuery());
        searchLogEntry.setQueryTook(context.getQueryTook());
        searchLogEntry.setTotal(context.getTotal());
        List<SearchResultItemLogEntry> resultItemLogEntries = new ArrayList<SearchResultItemLogEntry>();
        for (ResultItem resultItem: context.getResult()) {
            resultItemLogEntries.add(getResultItemLogEntry(resultItem));
        }
        searchLogEntry.setResultItems(resultItemLogEntries);
        searchLogEntry.setDate(new Date());
        write(searchLogEntry);
    }

    private SearchResultItemLogEntry getResultItemLogEntry(ResultItem resultItem) {
        SearchResultItemLogEntry resultItemLogEntry = new SearchResultItemLogEntry();
        resultItemLogEntry.setId(resultItem.getEntity().getId());
        resultItemLogEntry.setSiteId(resultItem.getEntity().getSiteId());
        resultItemLogEntry.setSchemaId(resultItem.getEntity().getSchemaId());
        resultItemLogEntry.setPosition(resultItem.getPosition());
        resultItemLogEntry.setScore(resultItem.getScore());
        resultItemLogEntry.setDocBoost(resultItem.getDocBoost());
        return resultItemLogEntry;
    }
}
