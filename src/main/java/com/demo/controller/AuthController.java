package com.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.domain.entity.GithubUser;
import com.demo.mapper.GithubUserMapper;
import com.demo.security.JwtUtil;
import com.demo.service.GithubUserService;
import com.mico.app.common.domain.vo.ExceptionVO;

import cn.hutool.core.bean.BeanUtil;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final GithubUserMapper githubUserMapper;
    private final GithubUserService githubUserService;

    public AuthController(JwtUtil jwtUtil, GithubUserMapper githubUserMapper, GithubUserService githubUserService) {
        this.jwtUtil = jwtUtil;
        this.githubUserMapper = githubUserMapper;
        this.githubUserService = githubUserService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Map<String, Object> map = new HashMap<>();
        if (authentication != null) {
            map.put("username", authentication.getName());
            GithubUser user = (GithubUser) authentication.getDetails();
            map.putAll(BeanUtil.beanToMap(user));
        }
        return map;
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("refresh_token is required");
        }

        Claims claims = jwtUtil.parseToken(refreshToken);
        String username = claims.getSubject();
        String id = (String) claims.get("id");
        GithubUser githubUser = githubUserService.getGithubUserById(id);

        if (Objects.isNull(githubUser)) {
            throw ExceptionVO.error("Invalid refresh_token: user not found");
        }
        // 这里使用与默认相同的过期时间重新生成一个新的 access_token
        String newAccessToken = jwtUtil.generateToken(username, BeanUtil.beanToMap(githubUser, true, true));
        Map<String, String> result = new HashMap<>();
        result.put("access_token", newAccessToken);
        return result;
    }
}
