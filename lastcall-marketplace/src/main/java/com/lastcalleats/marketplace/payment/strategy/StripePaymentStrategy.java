package com.lastcalleats.marketplace.payment.strategy;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.marketplace.order.entity.OrderDO;
import com.lastcalleats.marketplace.payment.dto.PaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StripePaymentStrategy implements PaymentStrategy {

    @Override
    public String getType() {
        return "STRIPE";
    }

    @Override
    public PaymentResponse pay(OrderDO order, String paymentMethodId) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (order.getPrice().doubleValue() * 100))
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .putAllMetadata(Map.of("orderId", String.valueOf(order.getId())))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return PaymentResponse.builder()
                    .status(intent.getStatus())
                    .intentId(intent.getId())
                    .build();
        } catch (StripeException e) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, e.getMessage());
        }
    }
}
