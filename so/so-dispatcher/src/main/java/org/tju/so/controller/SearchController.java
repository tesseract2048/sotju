package org.tju.so.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;
import org.tju.so.search.provider.SearchProvider;
import org.tju.so.service.HotwordService;
import org.tju.so.service.SearchLogService;

import com.google.gson.Gson;

@Controller
@RequestMapping(value = {
    "/api/search"
})
public class SearchController {

    @Autowired
    private SearchProvider searchProvider;

    @Autowired
    private SearchLogService searchLogService;

    @Autowired
    private HotwordService hotwordService;

    @RequestMapping(value = "/hotword", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String actionComplete(
            @RequestParam(value = "limit", required = false, defaultValue = "10") final int limit,
            HttpServletRequest req) throws Exception {
        List<String> hotwords = hotwordService.getHotwords(limit);
        return new Gson().toJson(hotwords);
    }

    @RequestMapping(value = "/complete", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String actionComplete(
            @RequestParam(value = "u", required = true) final String u,
            @RequestParam(value = "q", required = true) final String q,
            @RequestParam(value = "limit", required = false, defaultValue = "10") final int limit,
            HttpServletRequest req) throws Exception {
        List<String> completions = searchProvider.getCompletions(q, limit);
        searchLogService.writeComplete(u, q, limit, completions);
        return new Gson().toJson(completions);
    }

    @RequestMapping(value = "/click", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String clickAction(
            HttpServletRequest req,
            @RequestParam(value = "u", required = true) final String u,
            @RequestParam(value = "q", required = true) final String q,
            @RequestParam(value = "schemas", required = false) final String schemas,
            @RequestParam(value = "sites", required = false) final String sites,
            @RequestParam(value = "schemaId", required = true) final String schemaId,
            @RequestParam(value = "siteId", required = true) final String siteId,
            @RequestParam(value = "id", required = true) final String id,
            @RequestParam(value = "position", required = true) final int position)
            throws Exception {
        Query query = new Query(q);
        if (schemas != null)
            query.setSchemaIds(schemas.split(","));
        if (sites != null)
            query.setSiteIds(sites.split(","));
        searchLogService.writeClick(u, query, schemaId, siteId, id, position);
        return "{\"success\":true}";
    }

    @RequestMapping(value = "/query", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String actionSearch(
            @RequestParam(value = "u", required = true) final String u,
            @RequestParam(value = "q", required = true) final String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") final int start,
            @RequestParam(value = "limit", required = false, defaultValue = "15") final int limit,
            @RequestParam(value = "schemas", required = false) final String schemas,
            @RequestParam(value = "sites", required = false) final String sites,
            HttpServletRequest req) throws Exception {
        Query query = new Query(q);
        query.setStart(start);
        query.setLimit(limit);
        if (schemas != null)
            query.setSchemaIds(schemas.split(","));
        if (sites != null)
            query.setSiteIds(sites.split(","));
        Context context = searchProvider.search(query);
        context.setUserIdentifier(u);
        searchLogService.writeSearch(context);
        return new Gson().toJson(context);
    }
}
