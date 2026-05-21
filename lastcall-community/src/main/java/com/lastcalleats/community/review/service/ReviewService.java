package com.lastcalleats.community.review.service;

import com.lastcalleats.community.review.dto.CreateReviewRequest;
import com.lastcalleats.community.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {

    ReviewResponse createReview(Long userId, CreateReviewRequest request);

    Optional<ReviewResponse> getReviewByOrder(Long orderId);

    Page<ReviewResponse> listReviewsByMerchant(Long merchantId, Pageable pageable);

    Page<ReviewResponse> listReviewsByTemplate(Long templateId, Pageable pageable);
}
