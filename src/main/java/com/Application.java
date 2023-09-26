package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.mico.app.common.config.WebExceptionConfig;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.mapper")
@Import({ WebExceptionConfig.class })
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = { "com" })
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
