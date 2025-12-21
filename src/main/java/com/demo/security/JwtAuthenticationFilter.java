package com.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.domain.entity.GithubUser;
import com.demo.util.AuthorityUtils;
import com.demo.util.SecurityUtils;
import com.mico.app.common.domain.vo.RVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
                List<GrantedAuthority> authorities = authoritiesList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .sorted(Comparator.comparing(SimpleGrantedAuthority::getAuthority))
                        .collect(Collectors.toList());

                // 创建包含角色的User对象
                User principal = new User(username, "N/A", authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, claims, authorities);

                // 把额外信息放到 details 里，后续控制器可以从 Authentication.getDetails() 取
                authentication.setDetails(githubUser);

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
    private List<GrantedAuthority> getUserAuthorities(GithubUser githubUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        return authorities;
    }
}
