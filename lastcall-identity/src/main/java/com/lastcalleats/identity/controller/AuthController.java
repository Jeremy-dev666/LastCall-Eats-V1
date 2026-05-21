package com.lastcalleats.identity.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.identity.dto.*;
import com.lastcalleats.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/user")
    public ApiResponse<AuthResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        return ApiResponse.success(authService.registerUser(request));
    }

    @PostMapping("/register/merchant")
    public ApiResponse<AuthResponse> registerMerchant(@Valid @RequestBody MerchantRegisterRequest request) {
        return ApiResponse.success(authService.registerMerchant(request));
    }

    @PostMapping("/login/user")
    public ApiResponse<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.loginUser(request));
    }

    @PostMapping("/login/merchant")
    public ApiResponse<AuthResponse> loginMerchant(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.loginMerchant(request));
    }
}
