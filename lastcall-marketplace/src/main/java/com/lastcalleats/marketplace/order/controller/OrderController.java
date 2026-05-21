package com.lastcalleats.marketplace.order.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.marketplace.order.dto.OrderRequest;
import com.lastcalleats.marketplace.order.dto.OrderResponse;
import com.lastcalleats.marketplace.order.service.OrderService;
import com.lastcalleats.marketplace.order.service.PickupCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PickupCodeService pickupCodeService;

    @PostMapping("/api/orders")
    public ApiResponse<OrderResponse> createOrder(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.createOrder(userId, request));
    }

    @GetMapping("/api/orders")
    public ApiResponse<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(orderService.getUserOrders(userId));
    }

    @GetMapping("/api/orders/{id}")
    public ApiResponse<OrderResponse> getOrderDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetail(userId, id));
    }

    @GetMapping("/api/orders/{id}/pickup-code")
    public ApiResponse<OrderResponse> getPickupCode(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return ApiResponse.success(pickupCodeService.getPickupCode(userId, id));
    }

    @GetMapping("/api/merchant/orders")
    public ApiResponse<List<OrderResponse>> getMerchantOrders(@AuthenticationPrincipal Long merchantId) {
        return ApiResponse.success(orderService.getMerchantOrders(merchantId));
    }
}
