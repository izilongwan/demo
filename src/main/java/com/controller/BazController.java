package com.controller;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.event.BazEvent;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("baz")
public class BazController {
    @Resource
    ApplicationContext apllApplicationContext;

    @GetMapping("e")
    @ApiOperation(produces = MediaType.APPLICATION_JSON_VALUE, value = "")
    public String e(@RequestParam String param) {
        ApplicationEvent bazEvent = new BazEvent(param);
        apllApplicationContext.publishEvent(bazEvent);
        return param;
    }

}
