package com.lastcalleats.marketplace.payment.service;

import com.lastcalleats.marketplace.payment.dto.PaymentRequest;
import com.lastcalleats.marketplace.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(Long userId, PaymentRequest request);

    void handleWebhook(String payload, String sigHeader);
}
