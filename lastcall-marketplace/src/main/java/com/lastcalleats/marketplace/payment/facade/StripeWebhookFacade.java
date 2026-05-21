package com.lastcalleats.marketplace.payment.facade;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.marketplace.payment.dto.WebhookRequest;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeWebhookFacade {

    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;

    public WebhookRequest parseWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_FAILED, "Failed to deserialize webhook event"));

            Long orderId = Long.valueOf(intent.getMetadata().get("orderId"));

            return WebhookRequest.builder()
                    .eventId(event.getId())
                    .eventType(event.getType())
                    .orderId(orderId)
                    .build();
        } catch (SignatureVerificationException e) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "Invalid webhook signature");
        }
    }
}
