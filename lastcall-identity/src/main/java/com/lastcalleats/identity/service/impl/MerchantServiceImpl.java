package com.lastcalleats.identity.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.provider.ListingStatsProvider;
import com.lastcalleats.common.provider.OrderStatsProvider;
import com.lastcalleats.identity.dto.MerchantDashboardResponse;
import com.lastcalleats.identity.dto.MerchantProfileRequest;
import com.lastcalleats.identity.dto.MerchantProfileResponse;
import com.lastcalleats.identity.entity.MerchantDO;
import com.lastcalleats.identity.repository.MerchantRepo;
import com.lastcalleats.identity.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepo merchantRepo;
    private final OrderStatsProvider orderStatsProvider;
    private final ListingStatsProvider listingStatsProvider;

    @Override
    @Transactional(readOnly = true)
    public MerchantProfileResponse getProfile(Long merchantId) {
        return toResponse(findMerchant(merchantId));
    }

    @Override
    @Transactional
    public MerchantProfileResponse updateProfile(Long merchantId, MerchantProfileRequest request) {
        MerchantDO merchant = findMerchant(merchantId);
        if (request.getName() != null) merchant.setName(request.getName());
        if (request.getAddress() != null) merchant.setAddress(request.getAddress());
        if (request.getBusinessHours() != null) merchant.setBusinessHours(request.getBusinessHours());
        merchantRepo.save(merchant);
        return toResponse(merchant);
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantProfileResponse getPublicProfile(Long merchantId) {
        return toResponse(findMerchant(merchantId));
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantDashboardResponse getDashboard(Long merchantId) {
        findMerchant(merchantId);
        return MerchantDashboardResponse.builder()
                .todayOrderCount(orderStatsProvider.getTodayOrderCount(merchantId))
                .todayRevenue(orderStatsProvider.getTodayRevenue(merchantId))
                .activeListingCount(listingStatsProvider.getActiveListingCount(merchantId))
                .build();
    }

    private MerchantDO findMerchant(Long merchantId) {
        return merchantRepo.findById(merchantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MERCHANT_NOT_FOUND));
    }

    private MerchantProfileResponse toResponse(MerchantDO merchant) {
        return MerchantProfileResponse.builder()
                .id(merchant.getId())
                .email(merchant.getEmail())
                .name(merchant.getName())
                .address(merchant.getAddress())
                .businessHours(merchant.getBusinessHours())
                .isActive(merchant.getIsActive())
                .build();
    }
}
