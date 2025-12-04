package com.demo.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.map.MapUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

    private final String secret;

    private final long accessExpirationMillis;

    private final long refreshExpirationMillis;

    public JwtUtil(String secret, long accessExpirationMillis, long refreshExpirationMillis) {
        this.secret = secret;
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    public String generateToken(String subject) {
        return generateToken(subject, this.accessExpirationMillis);
    }

    public String generateToken(String subject, long customExpirationMillis) {
        return generateToken(subject, null, customExpirationMillis);
    }

    public String generateToken(String subject, Map<String, Object> extraClaims) {
        return generateToken(subject, extraClaims, this.accessExpirationMillis);
    }

    public String generateToken(String subject, Map<String, Object> extraClaims, long customExpirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + customExpirationMillis);
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder();
        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.setClaims(extraClaims);
        }
        return builder
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Map<String, String> generateAccessAndRefreshToken(String subject, Map<String, Object> extraClaims,
            long accessExpirationMillis, long refreshExpirationMillis) {
        long accessExp = accessExpirationMillis > 0 ? accessExpirationMillis : this.accessExpirationMillis;
        long refreshExp = refreshExpirationMillis > 0 ? refreshExpirationMillis : this.refreshExpirationMillis;
        String accessToken = generateToken(subject, extraClaims, accessExp);
        Map<String, Object> refreshExtra = MapUtil.getAny(extraClaims, "id");
        String refreshToken = generateToken(subject, refreshExtra, refreshExp);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        return map;
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
