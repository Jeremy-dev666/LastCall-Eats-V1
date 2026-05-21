package com.lastcalleats.marketplace.product.service;

import com.lastcalleats.marketplace.product.dto.ListingRequest;
import com.lastcalleats.marketplace.product.dto.ListingResponse;
import com.lastcalleats.marketplace.product.dto.UserBrowseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductListingService {

    ListingResponse create(Long merchantId, ListingRequest request);

    List<ListingResponse> getByMerchant(Long merchantId);

    void deactivate(Long merchantId, Long listingId);

    Page<UserBrowseResponse> browse(Pageable pageable);
}
