package com.example.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final String SECRET = "gateway-secret";

    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }
}
