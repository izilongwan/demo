package com.demo.test;

import org.junit.jupiter.api.Test;

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
    public void name1() {
        String key="""
                s
                %s
                %s
                """.formatted(1,"age");

        String n=switch(key){case"value","s"->"1";default->"d";};

        System.err.println(key);System.out.println(n);
    }
}
