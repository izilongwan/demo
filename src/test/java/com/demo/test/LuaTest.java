package com.demo.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.FileCopyUtils;

@SpringBootTest
public class LuaTest {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedisTemplate<Object, Long> redisTemplate;

    @Test
    public void returnLong() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/lua/demo.lua");
        byte[] bytes = FileCopyUtils.copyToByteArray(is);

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>(
                new String(bytes, StandardCharsets.UTF_8),
                Long.class);

        Long execute = stringRedisTemplate.execute(
                defaultRedisScript,
                Arrays.asList("id_1", "1"),
                "2", "3");
        System.out.println(execute);
    }

    @Test
    public void returnLists() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/lua/arr.lua");
        byte[] bytes = FileCopyUtils.copyToByteArray(is);

        DefaultRedisScript<List> defaultRedisScript = new DefaultRedisScript<>(
                new String(bytes, StandardCharsets.UTF_8), List.class);

        @SuppressWarnings("unchecked")
        List<List<Object>> execute = (List<List<Object>>) (List<?>) redisTemplate.execute(
                defaultRedisScript,
                Arrays.asList("id_1", "1"),
                2, 3);

        System.out.println(execute);
    }
}

class A {
    public int doit(int a, int b) {
        String[] str = new String[] { "", "dmoe" };
        return a + b;
    }

}

class A1 extends A implements IA {
    public static void main(String[] args) {
        IA a = new IA() {
            @Override
            public <T> T doit(T a, T b) {
                return null;
            }
        };
        Number n = a.doit(1, 1.0f);
    }

    public void doit() {

    }

    @Override
    public <T> T doit(T a, T b) {
        return a;
    }
}

@FunctionalInterface
interface IA {
    <T> T doit(T a, T b);
}
