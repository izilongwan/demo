package com.demo.security;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.demo.domain.entity.GithubUser;
import com.demo.service.GithubUserService;
import com.demo.util.AuthorityUtils;
import com.mico.app.api.code.service.ApiCodeService;
import com.mico.app.client.domain.dto.ApiParamDTO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    private final GithubUserService githubUserService;

    @Value("${github.front-redirect-url}")
    private String frontRedirectUrl;

    @Resource
    private ApiCodeService apiCodeService;

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

            userAttr.put(AuthorityUtils.USER_ID_FIELD, user.getId());
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
            tokenExtrInfo.put(AuthorityUtils.AUTHORITIES_KEY, getUserAuthorities(user.getId()));
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

    /**
     * 获取用户权限列表
     * 从数据库查询用户的角色和权限，并转换为GrantedAuthority列表
     */
    private List<String> getUserAuthorities(Long userId) {
        try {
            // 调用API获取用户权限数据
            Map<String, Object> paramMap = Collections.singletonMap(AuthorityUtils.USER_ID_FIELD, userId);
            ApiParamDTO apiParamDTO = new ApiParamDTO(AuthorityUtils.GET_GITHUB_RIGHTS_API, paramMap, null, null);
            List<Map<String, Object>> permissionData = apiCodeService.execSqlResponseList(apiParamDTO);

            if (permissionData == null || permissionData.isEmpty()) {
                // 如果没有权限数据，返回默认角色
                return Collections.singletonList(AuthorityUtils.DEFAULT_ROLE_USER);
            }

            // 使用Set去重并收集所有权限和角色
            Set<String> authorities = new HashSet<>();

            // 处理权限数据
            for (Map<String, Object> data : permissionData) {
                // 添加权限
                String permissionName = (String) data.get(AuthorityUtils.PERMISSION_NAME_FIELD);
                if (StringUtils.hasLength(permissionName)) {
                    authorities.add(permissionName.trim());
                }

                // 添加角色
                String roleName = (String) data.get(AuthorityUtils.ROLE_NAME_FIELD);
                if (StringUtils.hasLength(roleName)) {
                    authorities.add(roleName.trim());
                }
            }

            // 确保所有用户至少有USER角色
            authorities.add(AuthorityUtils.DEFAULT_ROLE_USER);

            // 转换为GrantedAuthority对象并排序（保证一致性）
            return authorities.stream()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // 权限查询失败时返回默认角色，避免登录失败
            return Collections.singletonList(AuthorityUtils.DEFAULT_ROLE_USER);
        }
    }
}
