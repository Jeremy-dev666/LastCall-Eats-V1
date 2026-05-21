package com.lastcalleats.marketplace.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private String status;
    private String intentId;
}
