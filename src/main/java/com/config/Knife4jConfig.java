package com.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Knife4jConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                // 这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo())
                .produces(Collections.singleton(MediaType.APPLICATION_JSON_VALUE));
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("这是knife4j API ")
                .description("# 这里记录服务端所有的接口的入参，出参等等信息")
                .contact(new Contact("izilongwan", "http://iyou.com", "js.nb@qq.com"))
                .version("3.0")
                .build();
    }

}
