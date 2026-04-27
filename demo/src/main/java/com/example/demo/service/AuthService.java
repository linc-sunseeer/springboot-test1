package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserService userService;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService, AdminMapper adminMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());

        String token = jwtService.generateUserToken(user.getId(), user.getName());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("accessToken", token);
        result.put("userType", "USER");
        result.put("name", user.getName());
        return result;
    }

    public Map<String, Object> adminLogin(LoginRequest request) {
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getEmail, request.getEmail())
                .last("limit 1"));

        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("メールまたはパスワードが正しくありません");
        }

        String token = jwtService.generateAdminToken(admin.getId(), admin.getName());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("accessToken", token);
        result.put("userType", "ADMIN");
        result.put("name", admin.getName());
        return result;
    }
}