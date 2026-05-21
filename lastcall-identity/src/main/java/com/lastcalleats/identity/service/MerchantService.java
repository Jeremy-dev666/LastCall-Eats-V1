package com.lastcalleats.identity.service;

import com.lastcalleats.identity.dto.MerchantDashboardResponse;
import com.lastcalleats.identity.dto.MerchantProfileRequest;
import com.lastcalleats.identity.dto.MerchantProfileResponse;

public interface MerchantService {

    MerchantProfileResponse getProfile(Long merchantId);

    MerchantProfileResponse updateProfile(Long merchantId, MerchantProfileRequest request);

    MerchantProfileResponse getPublicProfile(Long merchantId);

    MerchantDashboardResponse getDashboard(Long merchantId);
}
