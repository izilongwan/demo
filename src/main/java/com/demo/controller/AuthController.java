package com.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CreateCache;
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
    @Cached(name = "auth.me:", key = "#authentication?.getName()", expire = 300)
    public GithubUser me(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return GithubUser.builder().build();
        }

        GithubUser user = (GithubUser) authentication.getDetails();
        GithubUser githubUser = githubUserMapper.selectById(user.getId());

        return githubUser;
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (!StringUtils.hasText(refreshToken)) {
            throw ExceptionVO.error("refresh_token is required");
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

    @GetMapping("/set-redirect")
    public Boolean setSessionRedirectUrl(HttpServletRequest request, @RequestParam String redirectUrl) {
        HttpSession session = request.getSession();
        session.setAttribute(GithubUserService.REDIRECT_URL_KEY, redirectUrl);

        return Optional.ofNullable(session.getAttribute(GithubUserService.REDIRECT_URL_KEY))
                .map(Objects::isNull)
                .orElse(false);
    }
}
