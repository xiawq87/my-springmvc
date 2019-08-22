package com.xwq.controller;

import com.xwq.annotation.Controller;
import com.xwq.annotation.RequestMapping;
import com.xwq.annotation.RequestParam;

@Controller
public class TestController {

    @RequestMapping("/test")
    public String test() {
        return "my springmvc controller test";
    }

    @RequestMapping("/test2")
    public String test2(@RequestParam("name") String name) {
        return "hello test2: " + name;
    }

    @RequestMapping("/test3")
    public String test3(@RequestParam("name") String name, @RequestParam("pwd") String pwd) {
        return "hello test3-> name: " + name + " , pwd: " + pwd;
    }
}
