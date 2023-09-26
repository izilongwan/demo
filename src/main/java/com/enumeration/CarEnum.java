package com.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum CarEnum {
    BYD(101, "比亚迪", "中国") {
        @Override
        public void run() {
            System.out.println("闪亮登场，地表我最强 ===>");
            super.run();
            System.out.println("<=== 闪亮登场，地表我最强");
        }
    },

    LN(201, "雷诺", "法国"),
    XTL(202, "雪铁龙", "法国"),

    BM(301, "宝马", "德国"),
    BSJ(302, "保时捷", "德国"),
    BC(303, "奔驰", "德国"),

    FT(401, "福特", "美国"),
    LK(402, "林肯", "美国"),
    ;

    Integer id;
    String brand;
    String country;

    public void run() {
        System.out.println(String.format("我跑起来了 => %s", toString()));
    }
}
