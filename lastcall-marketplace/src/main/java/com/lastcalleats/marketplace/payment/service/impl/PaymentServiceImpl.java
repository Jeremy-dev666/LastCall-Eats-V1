package com.lastcalleats.marketplace.payment.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.util.Assert;
import com.lastcalleats.marketplace.order.entity.OrderDO;
import com.lastcalleats.marketplace.order.entity.PickupCodeDO;
import com.lastcalleats.marketplace.order.factory.PickupCodeFactory;
import com.lastcalleats.marketplace.order.repository.OrderRepo;
import com.lastcalleats.marketplace.order.repository.PickupCodeRepo;
import com.lastcalleats.marketplace.order.state.OrderState;
import com.lastcalleats.marketplace.payment.dto.PaymentRequest;
import com.lastcalleats.marketplace.payment.dto.PaymentResponse;
import com.lastcalleats.marketplace.payment.dto.WebhookRequest;
import com.lastcalleats.marketplace.payment.facade.StripeWebhookFacade;
import com.lastcalleats.marketplace.payment.service.PaymentService;
import com.lastcalleats.marketplace.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepo orderRepo;
    private final PickupCodeRepo pickupCodeRepo;
    private final PickupCodeFactory pickupCodeFactory;
    private final StripeWebhookFacade stripeWebhookFacade;
    private final List<PaymentStrategy> strategies;

    private Map<String, PaymentStrategy> strategyMap;

    private Map<String, PaymentStrategy> getStrategyMap() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(PaymentStrategy::getType, Function.identity()));
        }
        return strategyMap;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(Long userId, PaymentRequest request) {
        OrderDO order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Assert.equals(order.getUserId(), userId, ErrorCode.FORBIDDEN);

        OrderState state = OrderState.fromStatus(order.getOrderStatus());
        String newStatus = state.pay();
        order.setStatus(newStatus);

        PaymentStrategy strategy = getStrategyMap().get(request.getPaymentType());
        Assert.notNull(strategy, ErrorCode.PAYMENT_METHOD_NOT_SUPPORTED);

        PaymentResponse response = strategy.pay(order, request.getPaymentMethodId());

        PickupCodeDO pickupCode = pickupCodeFactory.generate("NUMERIC", order.getId());
        pickupCodeRepo.save(pickupCode);
        orderRepo.save(order);

        return response;
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        WebhookRequest webhook = stripeWebhookFacade.parseWebhook(payload, sigHeader);

        OrderDO order = orderRepo.findById(webhook.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        switch (webhook.getEventType()) {
            case "payment_intent.succeeded" -> {
                OrderState state = OrderState.fromStatus(order.getOrderStatus());
                order.setStatus(state.pay());
                PickupCodeDO pickupCode = pickupCodeFactory.generate("NUMERIC", order.getId());
                pickupCodeRepo.save(pickupCode);
            }
            case "payment_intent.payment_failed" -> {
                OrderState state = OrderState.fromStatus(order.getOrderStatus());
                order.setStatus(state.cancel());
            }
            default -> { return; }
        }

        orderRepo.save(order);
    }
}
