package com.example.demo.security;

// JWT 認證 filter：每次請求進來時檢查 Authorization header 裡的 JWT token
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public BearerTokenAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // JWT トークンを検証して Claims を取得
            Claims claims = jwtService.parseToken(token);
            if (claims != null) {
                String userId = claims.getSubject();
                String userType = claims.get("userType", String.class);

                if ("ADMIN".equals(userType)) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    token,
                                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
                } else {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    token,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER"))));
                }
            }
        }

        // 放行給下一層
        filterChain.doFilter(request, response);
    }
}