package com.demo.security;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.demo.domain.entity.GithubUser;
import com.demo.service.GithubUserService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    private final GithubUserService githubUserService;

    @Value("${github.front-redirect-url}")
    private String frontRedirectUrl;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, GithubUserService githubUserService) {
        this.jwtUtil = jwtUtil;
        this.githubUserService = githubUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            String username = String.valueOf(attributes.getOrDefault("login", "github-user"));

            Map<String, Object> userAttr = MapUtil.getAny(attributes, "node_id", "avatar_url");
            userAttr.put("login_username", username);
            userAttr.put("github_id", attributes.get("id"));

            GithubUser user = new QueryChainWrapper<GithubUser>(githubUserService.getBaseMapper())
                    .allEq(userAttr, true)
                    .one();

            if (Objects.isNull(user)) {
                user = BeanUtil.toBean(userAttr, GithubUser.class);
                // 新增 GitHub 用户信息
                boolean isOk = githubUserService.save(user);
            } else {
                // 已存在则更新信息
                BeanUtil.copyProperties(userAttr, user, true);
                boolean updateById = githubUserService.updateById(user);
            }

            userAttr.put("id", user.getId());
            String redirectUrl = Optional.ofNullable(request.getSession(false))
                    .map(s -> {
                        String key = GithubUserService.REDIRECT_URL_KEY;
                        Object url = s.getAttribute(key);
                        s.removeAttribute(key);
                        return url;
                    })
                    .map(Object::toString)
                    .orElse(frontRedirectUrl);
            Map<String, Object> tokenExtrInfo = MapUtil.getAny(userAttr, "id", "login_username", "avatar_url");
            Map<String, String> tokens = jwtUtil.generateAccessAndRefreshToken(username, tokenExtrInfo, 0L, 0L);
            String tokenParams = tokens.entrySet()
                    .stream()
                    .map(o -> String.format("%s=%s", o.getKey(), o.getValue()))
                    .collect(Collectors.joining("&"));
            // 将 token 作为 URL 参数返回
            String finalUrl = redirectUrl + (redirectUrl.contains("?") ? "&" : "?_=_&") + tokenParams;

            response.sendRedirect(finalUrl);
            // RVO<Map<String, String>> data = RVO.success(tokens);
            // response.setContentType("application/json;charset=UTF-8");
            // new ObjectMapper().writeValue(response.getWriter(), data);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
