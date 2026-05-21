package com.lastcalleats.community.review.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.common.response.PageResult;
import com.lastcalleats.community.review.dto.CreateReviewRequest;
import com.lastcalleats.community.review.dto.ReviewResponse;
import com.lastcalleats.community.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/reviews")
    public ApiResponse<ReviewResponse> createReview(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.success(reviewService.createReview(userId, request));
    }

    @GetMapping("/api/reviews/order/{orderId}")
    public ApiResponse<ReviewResponse> getReviewByOrder(@PathVariable Long orderId) {
        return ApiResponse.success(reviewService.getReviewByOrder(orderId).orElse(null));
    }

    @GetMapping("/api/reviews/merchant/{merchantId}")
    public ApiResponse<PageResult<ReviewResponse>> listReviewsByMerchant(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResult.of(
                reviewService.listReviewsByMerchant(merchantId, PageRequest.of(page - 1, size))));
    }

    @GetMapping("/api/reviews/template/{templateId}")
    public ApiResponse<PageResult<ReviewResponse>> listReviewsByTemplate(
            @PathVariable Long templateId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResult.of(
                reviewService.listReviewsByTemplate(templateId, PageRequest.of(page - 1, size))));
    }
}
