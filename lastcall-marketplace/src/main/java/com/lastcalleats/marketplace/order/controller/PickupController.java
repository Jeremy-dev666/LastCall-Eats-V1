package com.lastcalleats.marketplace.order.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.marketplace.order.dto.CodeRequest;
import com.lastcalleats.marketplace.order.dto.CodeResponse;
import com.lastcalleats.marketplace.order.service.PickupCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PickupController {

    private final PickupCodeService pickupCodeService;

    @PutMapping("/api/merchant/orders/verify")
    public ApiResponse<CodeResponse> verifyPickupCode(
            @AuthenticationPrincipal Long merchantId,
            @RequestBody CodeRequest request) {
        return ApiResponse.success(pickupCodeService.verifyPickupCode(merchantId, request));
    }
}
