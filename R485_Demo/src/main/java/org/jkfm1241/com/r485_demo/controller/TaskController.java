package org.jkfm1241.com.r485_demo.controller;


import org.jkfm1241.com.r485_demo.service.RequestQueryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/task")
public class TaskController {

    @Autowired
    private RequestQueryInfoService requestQueryInfoService;

    @GetMapping("/test")
    @ResponseBody
    public String test(){

        requestQueryInfoService.sendQuery();

        return "test";
    }

}
