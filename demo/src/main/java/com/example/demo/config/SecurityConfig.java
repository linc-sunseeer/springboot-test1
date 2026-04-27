package com.example.demo.config;

// 匯入自訂 bearer token filter，這個 filter 會在請求進來時判斷 token 身分
import com.example.demo.security.BearerTokenAuthenticationFilter;
// 匯入 Bean / Configuration，設定類與交給 Spring 管理的物件會用到
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// 匯入 BCrypt 與 PasswordEncoder，現在密碼 hash 就靠這個
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// 匯入 Spring Security 設定相關類別
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 這個類別是整個 Spring Security 的主要設定點
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 密碼編碼器 Bean
    // 之後 service 只要注入 PasswordEncoder，就會拿到 BCryptPasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 安全性過濾鏈
    // 白話就是：哪些 API 能直接進、哪些 API 要登入、哪些 API 只有 admin 能進
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, BearerTokenAuthenticationFilter bearerFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(bearerFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
