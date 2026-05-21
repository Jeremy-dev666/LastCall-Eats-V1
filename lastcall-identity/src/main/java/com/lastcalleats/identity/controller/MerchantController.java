package com.lastcalleats.identity.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.identity.dto.MerchantDashboardResponse;
import com.lastcalleats.identity.dto.MerchantProfileRequest;
import com.lastcalleats.identity.dto.MerchantProfileResponse;
import com.lastcalleats.identity.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/profile")
    public ApiResponse<MerchantProfileResponse> getProfile(@AuthenticationPrincipal Long merchantId) {
        return ApiResponse.success(merchantService.getProfile(merchantId));
    }

    @PutMapping("/profile")
    public ApiResponse<MerchantProfileResponse> updateProfile(
            @AuthenticationPrincipal Long merchantId,
            @Valid @RequestBody MerchantProfileRequest request) {
        return ApiResponse.success(merchantService.updateProfile(merchantId, request));
    }

    @GetMapping("/public/{merchantId}")
    public ApiResponse<MerchantProfileResponse> getPublicProfile(@PathVariable Long merchantId) {
        return ApiResponse.success(merchantService.getPublicProfile(merchantId));
    }

    @GetMapping("/dashboard")
    public ApiResponse<MerchantDashboardResponse> getDashboard(@AuthenticationPrincipal Long merchantId) {
        return ApiResponse.success(merchantService.getDashboard(merchantId));
    }
}
