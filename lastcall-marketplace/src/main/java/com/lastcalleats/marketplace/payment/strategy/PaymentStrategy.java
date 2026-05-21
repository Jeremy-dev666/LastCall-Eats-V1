package com.lastcalleats.marketplace.payment.strategy;

import com.lastcalleats.marketplace.order.entity.OrderDO;
import com.lastcalleats.marketplace.payment.dto.PaymentResponse;

public interface PaymentStrategy {

    String getType();

    PaymentResponse pay(OrderDO order, String paymentMethodId);
}
