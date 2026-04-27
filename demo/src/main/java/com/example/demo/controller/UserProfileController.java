package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserActionRequest;
import com.example.demo.entity.User;
import com.example.demo.security.CurrentUserService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final PasswordResetService passwordResetService;

    public UserProfileController(UserService userService,
                                  CurrentUserService currentUserService,
                                  PasswordResetService passwordResetService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping
    public ApiResponse<?> getProfile(Authentication authentication) {
        Long userId = currentUserService.requireUserId(authentication);
        return ApiResponse.ok(userService.getUserById(userId));
    }

    @PutMapping("/name")
    public ApiResponse<?> updateName(Authentication authentication, @RequestBody UserActionRequest request) {
        Long userId = currentUserService.requireUserId(authentication);
        User user = userService.updateUserName(userId, request.getName());
        return ApiResponse.ok("updated", user);
    }

    @PutMapping("/password")
    public ApiResponse<?> updatePassword(Authentication authentication, @RequestBody UserActionRequest request) {
        Long userId = currentUserService.requireUserId(authentication);
        User user = userService.updatePassword(userId, request.getPassword());
        return ApiResponse.ok("updated", user);
    }

    @PostMapping("/password/reset-request")
    public ApiResponse<?> requestPasswordReset(Authentication authentication) {
        Long userId = currentUserService.requireUserId(authentication);
        passwordResetService.createResetTokenForUser(userId);
        return ApiResponse.success("password reset requested");
    }

    @PutMapping("/diet-preferences")
    public ApiResponse<?> updateDietPreferences(Authentication authentication, @RequestBody UserActionRequest request) {
        Long userId = currentUserService.requireUserId(authentication);
        User user = userService.updateDietPreferences(userId, request.getTargetCalorie(), request.getAllergenSettings());
        return ApiResponse.ok("updated", user);
    }
}