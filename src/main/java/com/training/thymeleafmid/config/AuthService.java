package com.training.thymeleafmid.config;

import com.training.thymeleafmid.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    public void setCookie (HttpServletResponse response, LoginRequest request, boolean type) {

    }

    // true for student, false for teacher
    public void authenticateAndSetCookie(HttpServletResponse response, LoginRequest request, boolean type) {
        String userType = type ? "student:" : "teacher:";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userType+request.getId(), request.getPassword())
        );
        String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }
    public void deleteCookie(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
