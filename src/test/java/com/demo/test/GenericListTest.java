package com.demo.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class GenericListTest {
    @Test
    public void testList() {
        // 存放的是C及C的父级，添加的是C及C的子集，不可读取
        List<? super C> list = new ArrayList<>();

        list.add(new C());
        list.add(new E());
        System.out.println(list);

        // 存放的是C及C的子集，读取的是C及C的父级，不可添加
        List<? extends C> list2 = new ArrayList<C>() {
            {
                add(new E());
                add(new C());
            }
        };

        // System.out.println(list2);
        list2.stream().map(C::getLName).forEach(System.out::println);
    }

    public void sameStr(String sameStr) {
        System.out.println(sameStr);
        sameStr("1");
    }
}

@Data
class P {
    String fName;
}

@Data
@EqualsAndHashCode(callSuper = false)
class C extends P {
    String lName;
}

@Data
@EqualsAndHashCode(callSuper = false)
class E extends C {
    Integer name;
}
