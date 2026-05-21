package com.lastcalleats.marketplace.order.service;

import com.lastcalleats.marketplace.order.dto.CodeRequest;
import com.lastcalleats.marketplace.order.dto.CodeResponse;
import com.lastcalleats.marketplace.order.dto.OrderResponse;

public interface PickupCodeService {

    OrderResponse getPickupCode(Long userId, Long orderId);

    CodeResponse verifyPickupCode(Long merchantId, CodeRequest request);
}
