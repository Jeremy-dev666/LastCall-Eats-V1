package com.lastcalleats.marketplace.payment.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.marketplace.payment.dto.PaymentRequest;
import com.lastcalleats.marketplace.payment.dto.PaymentResponse;
import com.lastcalleats.marketplace.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ApiResponse<PaymentResponse> createPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PaymentRequest request) {
        return ApiResponse.success(paymentService.createPayment(userId, request));
    }

    @PostMapping("/webhook")
    public ApiResponse<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ApiResponse.success();
    }
}
