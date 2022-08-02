package com.learn.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * <a href="http://localhost:8090/tomcat/tomcat-test">访问路径</a>
 */
@Controller
public class TomcatController {

    @GetMapping("/tomcat-test")
    @ResponseBody
    public String tomcatTest() {
        System.out.println("tomcat test");
        return "tomcat test success";
    }

    @GetMapping("redirect")
    public String redirect() {
        return "redirect:/destination";
    }

    @GetMapping("destination")
    @ResponseBody
    public String destination() {
        return "destination";
    }

    @ResponseBody
    @GetMapping(value = "getMap", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public Object getMap() {
        return Map.of("a", 1, "b", 2);
    }
}
