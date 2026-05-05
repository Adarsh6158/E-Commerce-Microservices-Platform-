package com.ecommerce.auth_service.Config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    @Value("${auth.jwt.secret}")
    private String secret;

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Bean
    public JwtParser jwtParser(SecretKey jwtSecretKey) {
        return Jwts.parser().verifyWith(jwtSecretKey).build();
    }
}
