package com.lastcalleats.community.review.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.util.Assert;
import com.lastcalleats.community.review.dto.CreateReviewRequest;
import com.lastcalleats.community.review.dto.ReviewResponse;
import com.lastcalleats.community.review.entity.ReviewDO;
import com.lastcalleats.community.review.repository.ReviewRepo;
import com.lastcalleats.community.review.service.ReviewService;
import com.lastcalleats.marketplace.order.entity.OrderDO;
import com.lastcalleats.marketplace.order.repository.OrderRepo;
import com.lastcalleats.marketplace.product.entity.ProductListingDO;
import com.lastcalleats.marketplace.product.repository.ProductListingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepo;
    private final OrderRepo orderRepo;
    private final ProductListingRepo productListingRepo;

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
        OrderDO order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Assert.equals(order.getUserId(), userId, ErrorCode.FORBIDDEN);
        Assert.isTrue(order.getOrderStatus() == OrderDO.OrderStatus.COMPLETED, ErrorCode.REVIEW_NOT_ALLOWED);
        Assert.isTrue(!reviewRepo.existsByOrderId(request.getOrderId()), ErrorCode.REVIEW_ALREADY_EXISTS);

        ProductListingDO listing = productListingRepo.findById(order.getListingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LISTING_NOT_FOUND));

        ReviewDO review = ReviewDO.builder()
                .orderId(request.getOrderId())
                .userId(userId)
                .merchantId(order.getMerchantId())
                .templateId(listing.getTemplateId())
                .rating(request.getRating())
                .content(request.getContent())
                .imageUrls(request.getImageUrls())
                .build();
        reviewRepo.save(review);

        return toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewResponse> getReviewByOrder(Long orderId) {
        return reviewRepo.findByOrderId(orderId).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listReviewsByMerchant(Long merchantId, Pageable pageable) {
        return reviewRepo.findByMerchantIdAndIsVisibleTrueOrderByCreatedAtDesc(merchantId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listReviewsByTemplate(Long templateId, Pageable pageable) {
        return reviewRepo.findByTemplateIdAndIsVisibleTrueOrderByCreatedAtDesc(templateId, pageable)
                .map(this::toResponse);
    }

    private ReviewResponse toResponse(ReviewDO review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .orderId(review.getOrderId())
                .userId(review.getUserId())
                .userNickname(null)
                .userAvatarUrl(null)
                .merchantId(review.getMerchantId())
                .templateId(review.getTemplateId())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrls(review.getImageUrls())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
