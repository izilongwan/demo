package com.demo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alicp.jetcache.anno.Cached;
import com.demo.domain.entity.GithubUser;
import com.demo.service.AuthService;
import com.demo.util.AuthorityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    @Cached(name = "auth.me:", key = "#authentication?.getName()", expire = 300)
    public GithubUser me(Authentication authentication) {
        return authService.me(authentication);
    }

    @PostMapping("check-refresh-authoritity")
    public Map<String, String> checkRefreshAuthoritity(Authentication authentication,
            @RequestBody Map<String, String> body) {
        return authService.checkRefreshAuthoritity(authentication, body.get(AuthorityUtils.REFRESH_TOKEN));
    }

    @PostMapping("o/refresh/token")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        return authService.refresh(body.get(AuthorityUtils.REFRESH_TOKEN));
    }

    @GetMapping("o/set-redirect-url")
    public Boolean setSessionRedirectUrl(HttpServletRequest request, @RequestParam String redirectUrl) {
        return authService.setSessionRedirectUrl(request, redirectUrl);
    }
}
