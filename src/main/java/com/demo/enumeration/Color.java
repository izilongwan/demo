package com.demo.enumeration;

import java.util.stream.Stream;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum Color {
    RED(1, "This is red"),
    GREEN(2, "This is green"),
    BLUE(3, "This is blue") {
        @Override
        public void demo() {
            super.demo();
            System.out.println("ColorEnum.enclosing_method()");
        }
    };

    @EnumValue // 指定映射对象
    Integer id;
    String desc;

    public String getName() {
        return name();
    }

    @JsonValue // 指定序列化变量
    public String getDesc() {
        return desc;
    }

    Color(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public void demo() {
        System.out.println("ColorEnum.dome()");
    }

    public static Color getById(int id) {
        return Stream.of(Color.values())
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
