package com.demo.mapper;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.pojo.entity.Car;
import com.demo.pojo.entity.ManuFacturer;

@SpringBootTest
public class CarMapperTest {
    @Resource
    CarMapper carMapper;

    @Test
    void testSelectManuFacturers2() {
        ManuFacturer[] v = carMapper.selectManuFacturers2();

        for (ManuFacturer c : v) {
            System.out.println(c);
        }
    }

    @Test
    void testSelectManuFacturers() {
        ManuFacturer[] v = carMapper.selectManuFacturers();

        for (ManuFacturer c : v) {
            System.out.println(c);
        }
    }

    @Test
    public void update() {
        Car entity = Car.builder().colorId(3).build();
        LambdaQueryWrapper<Car> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntity(Car.builder().name("TK300").build());
        carMapper.update(entity, queryWrapper);
    }

    @Test
    public void add() {
        Car car = Car
                .builder()
                .name("TK300")
                .value("10011")
                .colorId(2)
                .lang(5.5)
                .build();

        carMapper.insert(car);
    }
}
