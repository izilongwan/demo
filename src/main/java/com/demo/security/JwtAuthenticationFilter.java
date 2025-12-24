package com.demo.security;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.domain.entity.GithubUser;
import com.demo.service.AuthService;
import com.demo.util.AuthorityUtils;
import com.demo.util.SecurityUtils;
import com.mico.app.common.domain.vo.RVO;
import com.mico.app.database.util.MicoAppDatabaseThreadLocalUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtUtil.parseToken(jwt);
            String username = claims.getSubject();

            if (Objects.nonNull(username) && Objects.isNull(SecurityUtils.getAuthentication())) {
                // 从JWT claims中获取用户信息
                GithubUser githubUser = BeanUtil.toBean(claims, GithubUser.class);

                // 获取用户角色 - 可以从数据库查询或JWT中提取
                List<String> authoritiesList = (List<String>) claims.get(AuthorityUtils.AUTHORITIES_KEY);

                // 创建包含角色的User对象
                UsernamePasswordAuthenticationToken authentication = authService.buildAuthentication(authoritiesList,
                        username);

                // 把额外信息放到 details 里，后续控制器可以从 Authentication.getDetails() 取
                authentication.setDetails(githubUser);

                githubUser.setAuthorities(authoritiesList);
                githubUser.setUpdateUser(githubUser.getLoginUsername());
                githubUser.setCreateUser(githubUser.getLoginUsername());
                MicoAppDatabaseThreadLocalUtil.set(MicoAppDatabaseThreadLocalUtil.SYSTEM_KEY,
                        BeanUtil.beanToMap(githubUser));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED,
                    "JWT token has expired");
            return;
        } catch (Exception ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, -10,
                    Objects.isNull(ex) ? "JWT token is invalid" : ex.toString());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, int code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String body = JSONUtil.toJsonStr(RVO.error(null, message, code));
        response.getWriter().write(body);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取用户权限列表
     * 包含角色(ROLE_*)和权限(authority)
     * 区分方式：
     * - 角色：以"ROLE_"前缀开头，如"ROLE_ADMIN"、"ROLE_USER"
     * - 权限：具体的操作权限，如"USER_READ"、"ADMIN_WRITE"
     */
}
