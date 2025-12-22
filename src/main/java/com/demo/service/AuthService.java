package com.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.demo.domain.entity.GithubUser;
import com.demo.domain.vo.AuthortityTokenVO;

public interface AuthService {
    public GithubUser me(Authentication authentication);

    public AuthortityTokenVO refreshAuthorityToken(String refreshToken);

    public List<String> getUserAuthorities(Long userId);

    public AuthortityTokenVO refreshAccessToken(String refreshToken);

    public void invalidMe(Authentication authentication);

    public Boolean setSessionRedirectUrl(HttpServletRequest request, String redirectUrl);

    UsernamePasswordAuthenticationToken buildAuthentication(List<String> authoritiesList, String username);
}
