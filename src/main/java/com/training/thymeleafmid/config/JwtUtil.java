package com.training.thymeleafmid.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

// a claim is any piece of personal information
// most common claims are sub -> subject (username) / iat -> issued at / exp -> expiring at
@Service
public class JwtUtil {

    // password exists in application.properties (.gitignored)
    @Value("${app.jwt.token}")
    private String password;

    private Key getSigningKey() {
        // decode stored password from application.properties
        byte[] keyBytes = Decoders.BASE64.decode(this.password);
        return Keys.hmacShaKeyFor(keyBytes); // takes raw bytes and returns a cryptographic key
    }

    public String generateToken(UserDetails userDetails) {
        final int expirationMs = 86400000;
        // set token information
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // short hand for writing claims -> claims.getSubject();
    }

    // function takes one input, and produces one output Fuction<Input, Output>
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // get all claims from token
        return claimsResolver.apply(claims); // ask for the cast you want to place the input in
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        // reverse building the token
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}




