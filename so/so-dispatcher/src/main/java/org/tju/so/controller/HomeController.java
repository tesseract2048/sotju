package org.tju.so.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = {
    "/"
})
public class HomeController {

    // @RequestMapping(value = "/home", method = RequestMethod.GET, produces =
    // "application/json; charset=utf-8")
    @ResponseBody
    public String callback(
            @RequestParam(value = "op", required = true) final String op,
            HttpServletRequest req) throws Exception {
        return op;
    }
}
