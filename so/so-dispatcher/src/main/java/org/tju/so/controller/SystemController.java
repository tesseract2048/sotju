package org.tju.so.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tju.so.model.notification.Notification.Receiver;
import org.tju.so.model.notification.Notification.Topic;
import org.tju.so.service.NotifyService;

@Controller
@RequestMapping(value = {
    "/api/system"
})
public class SystemController {

    @Autowired
    private NotifyService notifyService;

    @RequestMapping(value = "/notify/reload", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String notifyReload(HttpServletRequest req) throws Exception {
        notifyService.createNotification(Receiver.ALL, Topic.RELOAD, null);
        return "{\"success\":true}";
    }

    @RequestMapping(value = "/notify/push", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String notifyPush(
            HttpServletRequest req,
            @RequestParam(value = "url", required = true) final String url,
            @RequestParam(value = "postData", required = false) final String postData,
            @RequestParam(value = "priority", required = false) final Integer priority)
            throws Exception {
        Map<String, Object> target = new HashMap<String, Object>();
        target.put("url", url);
        if (postData != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("postData", postData);
            target.put("params", params);
        }
        notifyService.createNotification(Receiver.CRAWLER_SCHEDULER,
                Topic.PUSH, target);
        return "{\"success\":true}";
    }

}
