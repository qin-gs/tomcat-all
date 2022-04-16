package com.learn.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 访问路径 <a href="http://localhost:8090/tomcat/tomcat-test">http://localhost:8090/tomcat/tomcat-test</a>
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
    @GetMapping(value = "getMap", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getMap() {
        return Map.of("a", 1, "b", 2);
    }
}
