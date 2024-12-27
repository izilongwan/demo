package com.demo.controller;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.annotation.BarAnno;
import com.demo.annotation.CurrentUserAnno;
import com.demo.entity.Car;
import com.demo.entity.ManuFacturer;
import com.demo.entity.User;
import com.demo.enumeration.CarEnum;
import com.demo.mapper.CarMapper;
import com.demo.vo.CarVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "test")
@ApiSupport(author = "@izilong")
@Validated
public class TestController {
    @Resource
    CarMapper carMapper;

    @GetMapping("")
    @ApiOperationSupport(author = "zilong")
    @ApiOperation(notes = "没啥说的", value = "")
    @BarAnno
    public String test() {
        return "ok";
    }

    @ApiOperationSupport(author = "lee")
    @GetMapping("entity")
    public Car entity() {
        return Car.builder().name("Lee").build();
    }

    @PostMapping("add")
    public Car add(@RequestBody Car entity) {
        return entity;
    }

    @BarAnno
    @GetMapping("get")
    public String get(@RequestParam String a, @RequestParam String b) {
        return String.format("a => %s;b => %s", a, b);
    }

    @BarAnno
    @GetMapping(value = "v/{id}/{num}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String v(@PathVariable Integer id, @PathVariable Integer num) {
        return String.format("id => %d;num => %d", id, num);
    }

    @GetMapping("user")
    public User getUser(@CurrentUserAnno User user) {
        return user;
    }

    @GetMapping("car")
    public CarVO[] car(@RequestParam(required = false) CarEnum carEnum) {
        return buildCarVO(
                Optional
                        .ofNullable(carEnum)
                        .map(o -> new CarEnum[] { o })
                        .orElseGet(CarEnum::values));
    }

    private CarVO[] buildCarVO(CarEnum[] carEnums) {
        return Stream
                .of(carEnums)
                .map(o -> CarVO
                        .builder()
                        .name(o.name())
                        .id(o.getId())
                        .brand(o.getBrand())
                        .country(o.getCountry())
                        .build())
                .toArray(CarVO[]::new);

    }

    @GetMapping("manu")
    public ManuFacturer[] manu() {
        return carMapper.selectManuFacturers();
    }

    @GetMapping("manu2")
    public ManuFacturer[] manu2() {
        return carMapper.selectManuFacturers2();
    }

}
