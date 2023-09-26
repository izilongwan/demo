package com.entity;

import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.crypto.digest.DigestUtil;

public class CarTest {
    @Test
    public void test() {
        Wrappers
                .<Car>lambdaQuery()
                .select(Car::getName);

        Wrappers
                .lambdaQuery(Car.class)
                .select(Car::getLang);
    }

    @Test
    public void name() {
        String md5Hex = DigestUtil.sha256Hex("null");
        System.out.println(md5Hex);
    }

}
