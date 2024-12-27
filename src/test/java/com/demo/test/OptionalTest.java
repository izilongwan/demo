package com.demo.test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.demo.pojo.entity.Car;

public class OptionalTest {
    @Test
    public void test() {
        Car c = null;
        Optional<Car> ofNullable = Optional.ofNullable(c);

        ofNullable.orElse(c = Car.builder().name("LN").lang(5.4).build());
        ofNullable.orElseGet(Car::new);
        Double v = Optional
                .of(c)
                .map(Car::getName)
                .filter(o -> o.startsWith("LL"))
                .map(Double::valueOf)
                .orElse(Car.builder().lang(9.9).build().getLang());

        System.out.println(c);
        System.out.println(v);
    }

    @Test
    public void name() {
        Map<Object, Object> map = new HashMap<>();
        Map<Object, Map<Object, Object>> map2 = new HashMap<>();

        map.put("now", LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));

        map2.put("map", map);
        map.put("now2", LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
        System.out.println(map2);
    }
}
