package com.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.domain.entity.GithubUser;
import com.demo.domain.vo.AuthortityTokenVO;
import com.demo.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public GithubUser me(Authentication authentication) {
        return authService.me(authentication);
    }

    @PostMapping("o/refresh/authority-token")
    public AuthortityTokenVO refreshAuthorityToken(
            @Valid @RequestBody AuthortityTokenVO authortityTokenVO) {
        return authService.refreshAuthorityToken(authortityTokenVO.getRefreshToken());
    }

    @PostMapping("o/refresh/access-token")
    public AuthortityTokenVO refreshAccessToken(@Valid @RequestBody AuthortityTokenVO authortityTokenVO) {
        return authService.refreshAccessToken(authortityTokenVO.getRefreshToken());
    }

    @GetMapping("o/set-redirect-url")
    public Boolean setSessionRedirectUrl(HttpServletRequest request, @RequestParam String redirectUrl) {
        return authService.setSessionRedirectUrl(request, redirectUrl);
    }
}
