package com.lastcalleats.marketplace.product.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.common.response.PageResult;
import com.lastcalleats.marketplace.product.dto.ListingRequest;
import com.lastcalleats.marketplace.product.dto.ListingResponse;
import com.lastcalleats.marketplace.product.dto.UserBrowseResponse;
import com.lastcalleats.marketplace.product.service.ProductListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductListingController {

    private final ProductListingService listingService;

    @GetMapping("/api/products/browse")
    public ApiResponse<PageResult<UserBrowseResponse>> browse(Pageable pageable) {
        return ApiResponse.success(PageResult.of(listingService.browse(pageable)));
    }

    @PostMapping("/api/merchant/listings")
    public ApiResponse<ListingResponse> create(
            @AuthenticationPrincipal Long merchantId,
            @Valid @RequestBody ListingRequest request) {
        return ApiResponse.success(listingService.create(merchantId, request));
    }

    @GetMapping("/api/merchant/listings")
    public ApiResponse<List<ListingResponse>> list(@AuthenticationPrincipal Long merchantId) {
        return ApiResponse.success(listingService.getByMerchant(merchantId));
    }

    @DeleteMapping("/api/merchant/listings/{id}")
    public ApiResponse<Void> deactivate(
            @AuthenticationPrincipal Long merchantId,
            @PathVariable Long id) {
        listingService.deactivate(merchantId, id);
        return ApiResponse.success();
    }
}
