package com.demo.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.domain.entity.GithubUser;
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
                User principal = new User(username, "N/A", Collections.emptyList());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());

                // 把额外信息放到 details 里，后续控制器可以从 Authentication.getDetails() 取
                GithubUser u = BeanUtil.toBean(claims, GithubUser.class);
                authentication.setDetails(u);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, -1, "JWT token has expired");
            return;
        } catch (Exception ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, -2,
                    Objects.nonNull(ex) ? "JWT token is invalid" : ex.toString());
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
}
