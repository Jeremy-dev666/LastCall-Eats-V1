package com.lastcalleats.marketplace.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebhookRequest {

    private String eventId;
    private String eventType;
    private Long orderId;
}
