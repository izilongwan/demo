package com.demo.test;

import java.util.Objects;

public class Singleton {
    enum Ie {
        ;

        Ie() {
        }

        void doIt() {

        }
    }

    // 懒汉式
    private static final class Inner {
        static private Singleton INSTANCE = new Singleton();
    }

    static volatile Singleton INSTANCE = null;

    static Singleton INSTANCE2 = null;

    public static Singleton getInstance() {
        if (Objects.nonNull(INSTANCE)) {
            return INSTANCE;
        }

        synchronized (Singleton.class) {
            return Objects.nonNull(INSTANCE) ? INSTANCE : (INSTANCE = new Singleton());
        }
    }

    public static Singleton getInstance2() {
        return Inner.INSTANCE;
    }
}
