package com.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.demo.security.JwtAuthenticationFilter;
import com.demo.security.JwtUtil;
import com.demo.security.OAuth2LoginSuccessHandler;
import com.demo.service.GithubUserService;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.secret:demo-jwt-secret}")
    private String jwtSecret;

    @Value("${security.jwt.access-expiration-millis:900000}")
    private long accessExpirationMillis;

    @Value("${security.jwt.refresh-expiration-millis:604800000}")
    private long refreshExpirationMillis;

    // 从配置文件读取白名单路径
    @Value("${security.whitelist}")
    private String[] whitelist;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret, accessExpirationMillis, refreshExpirationMillis);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
            JwtUtil jwtUtil, GithubUserService githubUserService) throws Exception {

        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(whitelist)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .successHandler(new OAuth2LoginSuccessHandler(jwtUtil, githubUserService));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
