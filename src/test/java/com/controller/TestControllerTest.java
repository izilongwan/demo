package com.controller;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class TestControllerTest {
    @Test
    public void t() {
        new F<String>() {

            @Override
            public void demo(Consumer<String> consumer, String t) {
                consumer.accept(t);
            }

        }.demo(v -> {
            System.out.println(v);
        }, "ok");

        new V() {

            @Override
            public <T> T demo(Consumer<T> consumer, T t) {
                consumer.accept(t);
                return t;
            }

        }.demo(v -> {
            System.out.println(v);
        }, 101);

    }
}

/**
 * F
 */
@FunctionalInterface
interface F<T> {
    void demo(Consumer<T> consumer, T t);
}

/**
 * V
 */
@FunctionalInterface
interface V {
    <T> T demo(Consumer<T> consumer, T t);
}
