package com.lastcalleats.marketplace.order.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.provider.OrderStatsProvider;
import com.lastcalleats.marketplace.order.dto.OrderRequest;
import com.lastcalleats.marketplace.order.dto.OrderResponse;
import com.lastcalleats.marketplace.order.entity.OrderDO;
import com.lastcalleats.marketplace.order.entity.PickupCodeDO;
import com.lastcalleats.marketplace.order.factory.PickupCodeFactory;
import com.lastcalleats.marketplace.order.repository.OrderRepo;
import com.lastcalleats.marketplace.order.repository.PickupCodeRepo;
import com.lastcalleats.marketplace.order.service.OrderService;
import com.lastcalleats.marketplace.product.entity.ProductListingDO;
import com.lastcalleats.marketplace.product.entity.ProductTemplateDO;
import com.lastcalleats.marketplace.product.repository.ProductListingRepo;
import com.lastcalleats.marketplace.product.repository.ProductTemplateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService, OrderStatsProvider {

    private final OrderRepo orderRepo;
    private final PickupCodeRepo pickupCodeRepo;
    private final ProductListingRepo productListingRepo;
    private final ProductTemplateRepo productTemplateRepo;
    private final PickupCodeFactory pickupCodeFactory;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        ProductListingDO listing = productListingRepo.findById(request.getListingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LISTING_NOT_FOUND));

        if (!listing.getIsAvailable()) {
            throw new BusinessException(ErrorCode.LISTING_NOT_AVAILABLE);
        }

        if (listing.getRemainingQuantity() <= 0) {
            throw new BusinessException(ErrorCode.LISTING_SOLD_OUT);
        }

        boolean alreadyOrdered = orderRepo.existsByUserIdAndListingIdAndStatusNot(
                userId, listing.getId(), OrderDO.OrderStatus.CANCELLED.name());
        if (alreadyOrdered) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_EXISTS);
        }

        listing.setRemainingQuantity(listing.getRemainingQuantity() - 1);
        if (listing.getRemainingQuantity() == 0) {
            listing.setIsAvailable(false);
        }
        productListingRepo.save(listing);

        OrderDO order = OrderDO.builder()
                .userId(userId)
                .listingId(listing.getId())
                .merchantId(listing.getMerchantId())
                .price(listing.getDiscountPrice())
                .status(OrderDO.OrderStatus.PENDING_PAYMENT.name())
                .build();
        orderRepo.save(order);

        PickupCodeDO pickupCode = pickupCodeFactory.generate("NUMERIC", order.getId());
        pickupCodeRepo.save(pickupCode);

        ProductTemplateDO template = productTemplateRepo.findById(listing.getTemplateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND));

        return toResponse(order, template.getName(), pickupCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        List<OrderDO> orders = orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long userId, Long orderId) {
        OrderDO order = orderRepo.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return toResponseWithDetails(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMerchantOrders(Long merchantId) {
        List<OrderDO> orders = orderRepo.findByMerchantIdOrderByCreatedAtDesc(merchantId);
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    public int getTodayOrderCount(Long merchantId) {
        return orderRepo.countTodayOrdersByMerchantId(merchantId);
    }

    @Override
    public BigDecimal getTodayRevenue(Long merchantId) {
        return orderRepo.sumTodayRevenueByMerchantId(merchantId);
    }

    private OrderResponse toResponse(OrderDO order) {
        ProductTemplateDO template = productTemplateRepo.findById(
                productListingRepo.findById(order.getListingId())
                        .map(ProductListingDO::getTemplateId)
                        .orElse(0L)
        ).orElse(null);

        String productName = template != null ? template.getName() : null;

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .listingId(order.getListingId())
                .merchantId(order.getMerchantId())
                .productName(productName)
                .price(order.getPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderResponse toResponseWithDetails(OrderDO order) {
        ProductListingDO listing = productListingRepo.findById(order.getListingId()).orElse(null);
        ProductTemplateDO template = listing != null
                ? productTemplateRepo.findById(listing.getTemplateId()).orElse(null)
                : null;
        String productName = template != null ? template.getName() : null;

        PickupCodeDO pickupCode = pickupCodeRepo.findByOrderId(order.getId()).orElse(null);

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .listingId(order.getListingId())
                .merchantId(order.getMerchantId())
                .productName(productName)
                .price(order.getPrice())
                .status(order.getStatus())
                .numericCode(pickupCode != null ? pickupCode.getNumericCode() : null)
                .qrCode(pickupCode != null ? pickupCode.getQrCode() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderResponse toResponse(OrderDO order, String productName, PickupCodeDO pickupCode) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .listingId(order.getListingId())
                .merchantId(order.getMerchantId())
                .productName(productName)
                .price(order.getPrice())
                .status(order.getStatus())
                .numericCode(pickupCode != null ? pickupCode.getNumericCode() : null)
                .qrCode(pickupCode != null ? pickupCode.getQrCode() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
