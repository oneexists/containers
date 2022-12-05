package com.docker.containers.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.docker.containers.appUser.models.AppUserDetails;
import com.docker.containers.appUser.models.AppUserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtConverter {
    private final String TOKEN_PREFIX = "Bearer ";
    private final String DELIMITER = ",";
    private final String ISSUER = "bujo-bookshelf";
    private final int EXPIRATION_MINUTES = 15;
    private final int EXPIRATION_MILLIS = EXPIRATION_MINUTES * 60 * 1000;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String getTokenFromUser(AppUserDetails user) {
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(DELIMITER));
        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(user.getUsername())
                .claim("app_user_id", user.getAppUserId())
                .claim("authorities", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
                .signWith(key)
                .compact();
    }

    public AppUserDetails getUserFromToken(String token) {
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(ISSUER)
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.substring(TOKEN_PREFIX.length()));

            String username = jws.getBody().getSubject();
            Long appUserId = Long.parseLong(jws.getBody().get("app_user_id").toString());
            String authString = String.valueOf(jws.getBody().get("authorities"));
            AppUserRole appUserRole = getAuthorities(authString);

            return new AppUserDetails(appUserId, username, appUserRole);
        } catch (JwtException exception) {
            throw new JWTVerificationException("Token cannot be verified");
        }
    }

    private AppUserRole getAuthorities(String authString) {
        List<String> authorities = Arrays.asList(authString.split(DELIMITER));

        if (authorities.contains("ROLE_" + AppUserRole.ADMIN.name())) {
            return AppUserRole.ADMIN;
        }
        if (authorities.contains("ROLE_" + AppUserRole.USER.name())) {
            return AppUserRole.USER;
        }
        return null;
    }
}
