package com.lastcalleats.marketplace.order.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.marketplace.order.dto.CodeRequest;
import com.lastcalleats.marketplace.order.dto.CodeResponse;
import com.lastcalleats.marketplace.order.dto.OrderResponse;
import com.lastcalleats.marketplace.order.entity.OrderDO;
import com.lastcalleats.marketplace.order.entity.PickupCodeDO;
import com.lastcalleats.marketplace.order.repository.OrderRepo;
import com.lastcalleats.marketplace.order.repository.PickupCodeRepo;
import com.lastcalleats.marketplace.order.service.PickupCodeService;
import com.lastcalleats.marketplace.order.state.OrderState;
import com.lastcalleats.marketplace.product.entity.ProductListingDO;
import com.lastcalleats.marketplace.product.entity.ProductTemplateDO;
import com.lastcalleats.marketplace.product.repository.ProductListingRepo;
import com.lastcalleats.marketplace.product.repository.ProductTemplateRepo;
import com.lastcalleats.identity.entity.UserDO;
import com.lastcalleats.identity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PickupCodeServiceImpl implements PickupCodeService {

    private final OrderRepo orderRepo;
    private final PickupCodeRepo pickupCodeRepo;
    private final ProductListingRepo productListingRepo;
    private final ProductTemplateRepo productTemplateRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getPickupCode(Long userId, Long orderId) {
        OrderDO order = orderRepo.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        PickupCodeDO pickupCode = pickupCodeRepo.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_CODE_INVALID));

        ProductListingDO listing = productListingRepo.findById(order.getListingId()).orElse(null);
        ProductTemplateDO template = listing != null
                ? productTemplateRepo.findById(listing.getTemplateId()).orElse(null)
                : null;
        String productName = template != null ? template.getName() : null;

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .listingId(order.getListingId())
                .merchantId(order.getMerchantId())
                .productName(productName)
                .price(order.getPrice())
                .status(order.getStatus())
                .numericCode(pickupCode.getNumericCode())
                .qrCode(pickupCode.getQrCode())
                .createdAt(order.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public CodeResponse verifyPickupCode(Long merchantId, CodeRequest request) {
        PickupCodeDO pickupCode = pickupCodeRepo.findByNumericCodeAndMerchantId(
                        request.getPickupCode(), merchantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PICKUP_CODE_INVALID));

        if (pickupCode.getUsed()) {
            throw new BusinessException(ErrorCode.PICKUP_CODE_ALREADY_USED);
        }

        pickupCode.setUsed(true);
        pickupCodeRepo.save(pickupCode);

        OrderDO order = orderRepo.findById(pickupCode.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        OrderState state = OrderState.fromStatus(order.getOrderStatus());
        String newStatus = state.complete();
        order.setStatus(newStatus);
        orderRepo.save(order);

        UserDO user = userRepo.findById(order.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ProductListingDO listing = productListingRepo.findById(order.getListingId()).orElse(null);
        ProductTemplateDO template = listing != null
                ? productTemplateRepo.findById(listing.getTemplateId()).orElse(null)
                : null;
        String productName = template != null ? template.getName() : null;

        return CodeResponse.builder()
                .orderId(order.getId())
                .customerNickname(user.getNickname())
                .productName(productName)
                .success(true)
                .build();
    }
}
