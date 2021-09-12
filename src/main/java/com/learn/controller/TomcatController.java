package com.learn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 访问路径 http://localhost:8090/tomcat/tomcat-test
 */
@Controller
public class TomcatController {

    @GetMapping("/tomcat-test")
    @ResponseBody
    public String tomcatTest() {
        System.out.println("tomcat test");
        return "tomcat test success";
    }
}
