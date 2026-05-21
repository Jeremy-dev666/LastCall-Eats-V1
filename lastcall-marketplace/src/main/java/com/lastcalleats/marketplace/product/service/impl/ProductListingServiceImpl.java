package com.lastcalleats.marketplace.product.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.provider.ListingStatsProvider;
import com.lastcalleats.common.util.Assert;
import com.lastcalleats.identity.entity.MerchantDO;
import com.lastcalleats.identity.repository.MerchantRepo;
import com.lastcalleats.marketplace.product.dto.ListingRequest;
import com.lastcalleats.marketplace.product.dto.ListingResponse;
import com.lastcalleats.marketplace.product.dto.UserBrowseResponse;
import com.lastcalleats.marketplace.product.entity.ProductListingDO;
import com.lastcalleats.marketplace.product.entity.ProductTemplateDO;
import com.lastcalleats.marketplace.product.repository.ProductListingRepo;
import com.lastcalleats.marketplace.product.repository.ProductTemplateRepo;
import com.lastcalleats.marketplace.product.service.ProductListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductListingServiceImpl implements ProductListingService, ListingStatsProvider {

    private final ProductListingRepo listingRepo;
    private final ProductTemplateRepo templateRepo;
    private final MerchantRepo merchantRepo;

    @Override
    @Transactional
    public ListingResponse create(Long merchantId, ListingRequest request) {
        ProductTemplateDO template = templateRepo.findById(request.getTemplateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND));
        Assert.equals(template.getMerchantId(), merchantId, ErrorCode.FORBIDDEN);

        ProductListingDO listing = ProductListingDO.builder()
                .merchantId(merchantId)
                .templateId(template.getId())
                .discountPrice(request.getDiscountPrice())
                .quantity(request.getQuantity())
                .remainingQuantity(request.getQuantity())
                .pickupStart(request.getPickupStart())
                .pickupEnd(request.getPickupEnd())
                .date(request.getDate())
                .build();

        listingRepo.save(listing);
        return toResponse(listing, template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getByMerchant(Long merchantId) {
        return listingRepo.findByMerchantIdOrderByCreatedAtDesc(merchantId).stream()
                .map(listing -> {
                    ProductTemplateDO template = templateRepo.findById(listing.getTemplateId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND));
                    return toResponse(listing, template);
                })
                .toList();
    }

    @Override
    @Transactional
    public void deactivate(Long merchantId, Long listingId) {
        ProductListingDO listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LISTING_NOT_FOUND));
        Assert.equals(listing.getMerchantId(), merchantId, ErrorCode.FORBIDDEN);

        listing.setIsAvailable(false);
        listingRepo.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserBrowseResponse> browse(Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<ProductListingDO> listings = listingRepo.findByIsAvailableTrueAndDateOrderByCreatedAtDesc(today, pageable);

        return listings.map(listing -> {
            ProductTemplateDO template = templateRepo.findById(listing.getTemplateId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND));
            MerchantDO merchant = merchantRepo.findById(listing.getMerchantId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MERCHANT_NOT_FOUND));

            return UserBrowseResponse.builder()
                    .listingId(listing.getId())
                    .templateName(template.getName())
                    .templateDescription(template.getDescription())
                    .merchantName(merchant.getName())
                    .merchantAddress(merchant.getAddress())
                    .originalPrice(template.getOriginalPrice())
                    .discountPrice(listing.getDiscountPrice())
                    .remainingQuantity(listing.getRemainingQuantity())
                    .pickupStart(listing.getPickupStart())
                    .pickupEnd(listing.getPickupEnd())
                    .date(listing.getDate())
                    .build();
        });
    }

    @Override
    public int getActiveListingCount(Long merchantId) {
        return listingRepo.countByMerchantIdAndIsAvailableTrue(merchantId);
    }

    private ListingResponse toResponse(ProductListingDO listing, ProductTemplateDO template) {
        return ListingResponse.builder()
                .id(listing.getId())
                .merchantId(listing.getMerchantId())
                .templateId(listing.getTemplateId())
                .templateName(template.getName())
                .discountPrice(listing.getDiscountPrice())
                .originalPrice(template.getOriginalPrice())
                .quantity(listing.getQuantity())
                .remainingQuantity(listing.getRemainingQuantity())
                .pickupStart(listing.getPickupStart())
                .pickupEnd(listing.getPickupEnd())
                .date(listing.getDate())
                .isAvailable(listing.getIsAvailable())
                .createdAt(listing.getCreatedAt())
                .build();
    }
}
