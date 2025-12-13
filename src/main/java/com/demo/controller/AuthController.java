package com.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alicp.jetcache.anno.Cached;
import com.demo.domain.entity.GithubUser;
import com.demo.mapper.GithubUserMapper;
import com.demo.security.JwtUtil;
import com.demo.service.GithubUserService;
import com.demo.util.AuthorityUtils;
import com.mico.app.common.domain.vo.ExceptionVO;

import cn.hutool.core.bean.BeanUtil;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final GithubUserMapper githubUserMapper;
    private final GithubUserService githubUserService;

    @Value("${github.authorization-url}")
    private String githubAuthorizationUrl;

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
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        githubUser.setAuthorities(authorities);
        return githubUser;
    }

    @PostMapping("o/refresh/token")
    public Map<String, String> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get(AuthorityUtils.REFRESH_TOKEN);
        if (!StringUtils.hasText(refreshToken)) {
            throw ExceptionVO.error(AuthorityUtils.REFRESH_TOKEN + " is required");
        }

        Claims claims = jwtUtil.parseToken(refreshToken);
        String username = claims.getSubject();
        Long id = (Long) claims.get(AuthorityUtils.USER_ID_FIELD);
        GithubUser githubUser = githubUserService.getGithubUserById(id);

        if (Objects.isNull(githubUser)) {
            ExceptionVO evo = ExceptionVO.error(githubAuthorizationUrl,
                    "Invalid " + AuthorityUtils.REFRESH_TOKEN + ": user not found",
                    HttpServletResponse.SC_UNAUTHORIZED);
            evo.setStatus(HttpStatus.UNAUTHORIZED);
            throw evo;
        }
        Map<String, Object> extraInfo = BeanUtil.beanToMap(githubUser, true, true);
        extraInfo.put(AuthorityUtils.AUTHORITIES_KEY, claims.get(AuthorityUtils.AUTHORITIES_KEY));
        // 这里使用与默认相同的过期时间重新生成一个新的 access_token
        String newAccessToken = jwtUtil.generateToken(username, extraInfo);
        Map<String, String> result = new HashMap<>();
        result.put(AuthorityUtils.ACCESS_TOKEN, newAccessToken);
        return result;
    }

    @GetMapping("o/set-redirect-url")
    public Boolean setSessionRedirectUrl(HttpServletRequest request, @RequestParam String redirectUrl) {
        HttpSession session = request.getSession();
        session.setAttribute(GithubUserService.REDIRECT_URL_KEY, redirectUrl);

        return Optional.ofNullable(session.getAttribute(GithubUserService.REDIRECT_URL_KEY))
                .map(Objects::nonNull)
                .orElse(false);
    }
}
