package com.demo.controller;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.pojo.entity.User;
import com.demo.pojo.vo.CarVO;

@RestController
@Validated
@RequestMapping("v")
public class ValidateController implements InitializingBean {
    @Resource
    @Lazy
    ValidateController validateController;

    final ValidateController validateController2;

    @Autowired
    @Lazy
    public ValidateController(ValidateController validateController) {
        this.validateController2 = validateController;
    }

    @GetMapping("1")
    public String v(@NotNull Integer id, @NotEmpty String name) {
        return String.format("id => %s, name => %s", id, name);
    }

    @GetMapping("2")
    public User v2(@Validated User user) {
        return user;
    }

    @PostMapping("3")
    public User v3(@Validated @RequestBody User user) {
        return user;
    }

    @GetMapping("n")
    public Long n(@NotNull @PositiveOrZero Long num) {
        return num;
    }

    @GetMapping("carVO")
    public CarVO car(@Validated CarVO carVO) {
        return carVO;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
