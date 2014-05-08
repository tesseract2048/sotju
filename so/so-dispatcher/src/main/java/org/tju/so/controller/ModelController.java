package org.tju.so.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;

import com.google.gson.Gson;

@Controller
@RequestMapping(value = {
    "/api/model"
})
public class ModelController {

    @Autowired
    private SchemaHolder schemaHolder;

    @Autowired
    private SiteHolder siteHolder;

    @RequestMapping(value = "/schema/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String schema(@PathVariable final String id, HttpServletRequest req)
            throws Exception {
        return new Gson().toJson(schemaHolder.get(id));
    }

    @RequestMapping(value = "/site/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String site(@PathVariable final String id, HttpServletRequest req)
            throws Exception {
        return new Gson().toJson(siteHolder.get(id));
    }
}
