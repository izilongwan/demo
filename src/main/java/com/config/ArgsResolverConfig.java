package com.config;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.resolver.CurrentUserResolver;

@Configuration
public class ArgsResolverConfig implements WebMvcConfigurer {
    @Resource
    CurrentUserResolver currentUserResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserResolver);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

}
