package com.demo.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

/**
 * 权限工具类
 * 用于处理角色和权限的区分与检查
 */
public class AuthorityUtils {

    // Token相关常量
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String AUTHORITIES_KEY = "authorities";

    // 用户ID字段常量
    public static final String USER_ID_FIELD = "id";

    // API编码常量
    public static final String GET_GITHUB_RIGHTS_API = "GET_GITHUB_RIGHTS";

    // 默认角色常量
    public static final String DEFAULT_ROLE_USER = "ROLE_USER";

    // 数据库字段常量
    public static final String PERMISSION_NAME_FIELD = "permission_name";
    public static final String ROLE_NAME_FIELD = "role_name";

    /**
     * 从authorities中提取角色（不包含ROLE_前缀）
     *
     * @param authorities 用户的所有权限列表
     * @return 角色列表，如["ADMIN", "USER"]
     */
    public static List<String> extractRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5)) // 移除"ROLE_"前缀
                .collect(Collectors.toList());
    }

    /**
     * 从authorities中提取权限（不包含角色）
     *
     * @param authorities 用户的所有权限列表
     * @return 权限列表，如["USER_READ", "ADMIN_WRITE"]
     */
    public static List<String> extractPermissions(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_"))
                .collect(Collectors.toList());
    }

    /**
     * 检查是否有指定角色
     *
     * @param authorities 用户权限列表
     * @param role        角色名（不含ROLE_前缀）
     * @return 是否有该角色
     */
    public static boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream()
                .anyMatch(authority -> ("ROLE_" + role).equals(authority.getAuthority()));
    }

    /**
     * 检查是否有指定权限
     *
     * @param authorities 用户权限列表
     * @param permission  权限名
     * @return 是否有该权限
     */
    public static boolean hasPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        return authorities.stream()
                .anyMatch(authority -> permission.equals(authority.getAuthority()));
    }

    /**
     * 获取所有权限的字符串表示
     *
     * @param authorities 用户权限列表
     * @return 格式化的权限字符串
     */
    public static String formatAuthorities(Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = extractRoles(authorities);
        List<String> permissions = extractPermissions(authorities);

        StringBuilder sb = new StringBuilder();
        sb.append("Roles: ").append(roles);
        sb.append(", Permissions: ").append(permissions);
        return sb.toString();
    }
}
