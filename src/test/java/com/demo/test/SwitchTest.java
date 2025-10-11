package com.demo.test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.demo.domain.entity.Car;

public class SwitchTest {
    @Test
    public void name() {
        // int string enum
        String key = "true";
        switch (key) {
            case "0":
                break;

            default:
                break;
        }
    }

    @Test
    public void name2() {
        System.out.println(12);
    }

    @Test
    public void sortBy() {
        TreeSet<Car> carSet = new TreeSet<>(Comparator.comparing(Car::getLang).reversed());

        // 添加car就行排序
        carSet.add(new Car(1, "a", 1.0, "", 1, "", LocalDateTime.now()));
        carSet.add(new Car(2, "b", 2.0, "", 2, "", LocalDateTime.now()));
        carSet.add(new Car(3, "c", 3.0, "", 3, "", LocalDateTime.now()));
        System.out.println(carSet);
        double[] array = carSet.stream().mapToDouble(Car::getLang).toArray();
        double[] actual = new double[] { 3, 2, 1 };
        Assertions.assertArrayEquals(array, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 3, 5, '5,3,1'",
            "4.0, 5.9, 4.3, '5.9,4.3,4.1'" })
    public void sortBy2(double d1, double d2, double d3, String s) {
        TreeSet<Car> carSet = new TreeSet<>(Comparator.comparing(Car::getLang).reversed());

        // 添加car就行排序
        carSet.add(new Car(1, "a", d1, "", 1, "", LocalDateTime.now()));
        carSet.add(new Car(2, "b", d2, "", 2, "", LocalDateTime.now()));
        carSet.add(new Car(3, "c", d3, "", 3, "", LocalDateTime.now()));
        System.out.println(carSet);
        double[] array = carSet.stream().mapToDouble(Car::getLang).toArray();
        double[] actual = Stream.of(s.split(",")).mapToDouble(Double::parseDouble).toArray();
        Assertions.assertArrayEquals(array, actual);
    }

    @Test
    public void name1() {
        // String key="""
        // s
        // %s
        // %s
        // """.formatted(1,"age");

        // String n=switch(key){case"value","s"->"1";default->"d";};

        // System.err.println(key);System.out.println(n);
    }
}
