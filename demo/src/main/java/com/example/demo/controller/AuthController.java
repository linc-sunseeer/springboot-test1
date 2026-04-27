package com.example.demo.controller;

// 匯入登入 / 註冊 / 共用動作 DTO，這些是前端送進來的資料格式
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserActionRequest;
// 匯入 User 實體，註冊時會先組成 User 再交給 service 處理
import com.example.demo.entity.User;
// 匯入 service，Controller 主要是接請求，真正的業務邏輯交給 service
import com.example.demo.service.AuthService;
import com.example.demo.service.EmailVerificationService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
// 匯入 Spring Web 註解，這些是在告訴 Spring 哪個方法對應哪個 API
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 匯入 Map，這裡先用簡單 Map 當 API 回傳格式
import java.util.Map;

import com.example.demo.dto.ApiResponse;

// 這個 Controller 專門處理「登入 / 註冊 / 忘記密碼」這些認證相關入口
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService,
                          UserService userService,
                          PasswordResetService passwordResetService,
                          EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.emailVerificationService = emailVerificationService;
    }

    // 一般使用者登入
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        return ApiResponse.ok("ログインしました", authService.login(request));
    }

    // 管理者登入
    @PostMapping("/admin/login")
    public ApiResponse<?> adminLogin(@RequestBody LoginRequest request) {
        return ApiResponse.ok("ログインしました", authService.adminLogin(request));
    }

    // 使用者註冊
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User createdUser = userService.createUser(user);
        emailVerificationService.sendVerificationEmail(createdUser);

        return ApiResponse.ok("登録完了", createdUser);
    }

    @org.springframework.web.bind.annotation.GetMapping("/verify-email")
    public ApiResponse<?> verifyEmail(@org.springframework.web.bind.annotation.RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ApiResponse.success("メール認証が完了しました");
    }

    // 忘記密碼：先申請 reset token
    @PostMapping("/password/reset-request")
    public ApiResponse<?> requestPasswordReset(@RequestBody UserActionRequest request) {
        passwordResetService.createResetToken(request.getEmail());
        return ApiResponse.success("再設定リンクを送信しました");
    }

    // 使用 token 真正重設密碼
    @PostMapping("/password/reset")
    public ApiResponse<?> resetPassword(@RequestBody UserActionRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getPassword());
        return ApiResponse.success("パスワードを再設定しました");
    }
}
