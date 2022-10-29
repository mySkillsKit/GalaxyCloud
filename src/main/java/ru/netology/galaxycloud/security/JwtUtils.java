package ru.netology.galaxycloud.security;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.netology.galaxycloud.entities.UserRole;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<UserRole> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
    }
}