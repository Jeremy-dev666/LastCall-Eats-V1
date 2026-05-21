package com.lastcalleats.marketplace.order.service;

import com.lastcalleats.marketplace.order.dto.OrderRequest;
import com.lastcalleats.marketplace.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(Long userId, OrderRequest request);

    List<OrderResponse> getUserOrders(Long userId);

    OrderResponse getOrderDetail(Long userId, Long orderId);

    List<OrderResponse> getMerchantOrders(Long merchantId);
}
