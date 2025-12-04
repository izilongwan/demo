package com.demo.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.demo.domain.entity.GithubUser;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static GithubUser getCurrentGithubUser() {
        return Optional.ofNullable(getAuthentication())
                .map(Authentication::getDetails)
                .filter(details -> details instanceof GithubUser)
                .map(details -> (GithubUser) details)
                .orElse(null);
    }
}
