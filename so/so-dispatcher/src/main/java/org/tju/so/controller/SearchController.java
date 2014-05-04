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

import com.google.gson.Gson;

@Controller
@RequestMapping(value = {
    "/"
})
public class SearchController {

    @Autowired
    private SearchProvider searchProvider;

    @RequestMapping(value = "/complete", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String actionComplete(
            @RequestParam(value = "q", required = true) final String q,
            @RequestParam(value = "limit", required = false, defaultValue = "10") final int limit,
            HttpServletRequest req) throws Exception {
        List<String> completions = searchProvider.getCompletions(q, limit);
        return new Gson().toJson(completions);
    }

    @RequestMapping(value = "/search", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String actionSearch(
            @RequestParam(value = "q", required = true) final String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") final int start,
            @RequestParam(value = "limit", required = false, defaultValue = "15") final int limit,
            HttpServletRequest req) throws Exception {
        Query query = new Query(q);
        query.setStart(start);
        query.setLimit(limit);
        Context context = searchProvider.search(query);
        return new Gson().toJson(context);
    }
}
