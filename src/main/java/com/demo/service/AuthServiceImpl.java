package com.demo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.demo.domain.entity.GithubUser;
import com.demo.mapper.GithubUserMapper;
import com.demo.security.JwtUtil;
import com.demo.util.AuthorityUtils;
import com.mico.app.api.code.service.ApiCodeService;
import com.mico.app.client.domain.dto.ApiParamDTO;
import com.mico.app.common.domain.vo.ExceptionVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final GithubUserMapper githubUserMapper;
    private final GithubUserService githubUserService;
    private final ApiCodeService apiCodeService;
    private final AuthService authService;

    @Value("${github.authorization-url}")
    private String githubAuthorizationUrl;

    public AuthServiceImpl(JwtUtil jwtUtil, GithubUserMapper githubUserMapper, GithubUserService githubUserService,
            ApiCodeService apiCodeService, @Lazy AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.githubUserMapper = githubUserMapper;
        this.githubUserService = githubUserService;
        this.apiCodeService = apiCodeService;
        this.authService = authService;
    }

    @Override
    @Cached(name = "auth.me:", key = "#authentication?.getName()", expire = 300)
    public GithubUser me(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return GithubUser.builder().build();
        }

        GithubUser user = (GithubUser) authentication.getDetails();
        GithubUser githubUser = githubUserMapper.selectById(user.getId());

        List<String> newAuthorities = getUserAuthorities(githubUser.getId());

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean authoritityHasChange = checkAuthoritityChange(newAuthorities, authorities);
        if (authoritityHasChange) {
            Map<String, Object> userAttr = BeanUtil.beanToMap(githubUser, true, true);
            Map<String, Object> tokenExtrInfo = MapUtil.getAny(userAttr, "id", "login_username", "avatar_url");
            tokenExtrInfo.put(AuthorityUtils.AUTHORITIES_KEY, newAuthorities);

            if (authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Instant expiresAt = jwt.getExpiresAt(); // 这里就是过期时间
                System.out.println("过期时间: " + expiresAt);
            }
            Map<String, String> tokens = jwtUtil.generateAccessAndRefreshToken(githubUser.getLoginUsername(),
                    tokenExtrInfo, 0L, 0L);
        }

        githubUser.setAuthorities(newAuthorities);
        return githubUser;
    }

    public Map<String, String> checkRefreshAuthoritity(Authentication authentication, String refreshToken) {
        if (Objects.isNull(authentication)) {
            return null;
        }

        GithubUser user = (GithubUser) authentication.getDetails();
        GithubUser githubUser = githubUserMapper.selectById(user.getId());
        List<String> newAuthorities = getUserAuthorities(githubUser.getId());

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean authoritityHasChange = checkAuthoritityChange(newAuthorities, authorities);
        if (authoritityHasChange) {
            Map<String, Object> userAttr = BeanUtil.beanToMap(githubUser, true, true);
            Map<String, Object> tokenExtrInfo = MapUtil.getAny(userAttr, "id", "login_username", "avatar_url");
            tokenExtrInfo.put(AuthorityUtils.AUTHORITIES_KEY, newAuthorities);

            long diffSecond = 0;
            Integer expSecond = Optional.ofNullable(jwtUtil.parseToken(refreshToken))
                    .map(o -> (Map<String, Object>) o)
                    .map(o -> o.get("exp"))
                    .map(o -> ((int) o))
                    .orElse(0);
            System.out.println(GithubUser.Fields.loginUsername);
            if (expSecond > 0) {
                log.debug("过期时间: " + LocalDateTime.ofEpochSecond(expSecond, 0, ZoneOffset.ofHours(8))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                diffSecond = expSecond - LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
                authService.invalidMe(authentication);
                return jwtUtil.generateAccessAndRefreshToken(githubUser.getLoginUsername(),
                        tokenExtrInfo, 0, diffSecond * 1000);
            }
        }

        return null;
    }

    @CacheInvalidate(name = "auth.me:", key = "#authentication?.getName()")
    public void invalidMe(Authentication authentication) {
        log.debug("authoritity change");
    }

    private boolean checkAuthoritityChange(List<String> newAuthorities, List<String> authorities) {
        boolean hasChange = false;
        for (int i = 0; i < newAuthorities.size(); i++) {
            if (!newAuthorities.get(i).equals(authorities.get(i))) {
                hasChange = true;
                log.debug("[用户]权限发生变化");
                break;
            }
        }

        return hasChange;
    }

    /**
     * 获取用户权限列表
     * 从数据库查询用户的角色和权限，并转换为GrantedAuthority列表
     */
    @Override
    public List<String> getUserAuthorities(Long userId) {
        try {
            // 调用API获取用户权限数据
            Map<String, Object> paramMap = Collections.singletonMap(AuthorityUtils.USER_ID_FIELD, userId);
            ApiParamDTO apiParamDTO = ApiParamDTO.builder()
                    .apiCode(AuthorityUtils.GET_GITHUB_RIGHTS_API)
                    .param(paramMap)
                    .build();
            List<Map<String, Object>> permissionData = apiCodeService.getList(apiParamDTO);

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
                    .sorted()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // 权限查询失败时返回默认角色，避免登录失败
            log.error("%s [error] ", "权限查询失败", e.getLocalizedMessage());
            return Collections.singletonList(AuthorityUtils.DEFAULT_ROLE_USER);
        }
    }

    @Override
    public Map<String, String> refresh(String refreshToken) {
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

    @Override
    public Boolean setSessionRedirectUrl(HttpServletRequest request, String redirectUrl) {
        HttpSession session = request.getSession();
        session.setAttribute(GithubUserService.REDIRECT_URL_KEY, redirectUrl);

        return Optional.ofNullable(session.getAttribute(GithubUserService.REDIRECT_URL_KEY))
                .map(Objects::nonNull)
                .orElse(false);
    }
}
