package com.lastcalleats.identity.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.identity.dto.UserProfileRequest;
import com.lastcalleats.identity.dto.UserProfileResponse;
import com.lastcalleats.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(userId, request));
    }

    @PostMapping("/avatar")
    public ApiResponse<UserProfileResponse> uploadAvatar(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.uploadAvatar(userId, file));
    }
}
