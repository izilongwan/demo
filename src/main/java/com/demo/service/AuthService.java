package com.demo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

import com.demo.domain.entity.GithubUser;

public interface AuthService {
    public GithubUser me(Authentication authentication);

    public Map<String, String> checkRefreshAuthoritity(Authentication authentication, String refreshToken);

    public List<String> getUserAuthorities(Long userId);

    public Map<String, String> refresh(String refreshToken);

    public void invalidMe(Authentication authentication);

    public Boolean setSessionRedirectUrl(HttpServletRequest request, String redirectUrl);
}
