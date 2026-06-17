package com.admin.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Forwards non-API, non-static paths to index.html for React Router SPA support.
 */
@Controller
public class SpaForwardController {

    @RequestMapping(value = {
            "/app/**",
            "/dashboard/**",
            "/system/**",
            "/file/**",
            "/notification/**",
            "/schedule/**",
            "/project/**",
            "/report/**",
            "/seal/**",
            "/bank/**",
            "/assistant/**",
            "/order/**",
            "/survey/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
