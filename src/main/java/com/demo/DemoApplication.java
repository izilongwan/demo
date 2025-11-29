package com.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.mico.app.database.config.MybatisPlusConfig;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.demo.mapper")
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = { "com.demo" })
@Import({ MybatisPlusConfig.class })
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
