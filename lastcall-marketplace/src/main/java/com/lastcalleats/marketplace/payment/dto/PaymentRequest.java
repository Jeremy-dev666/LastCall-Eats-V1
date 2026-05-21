package com.lastcalleats.marketplace.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long orderId;

    private String paymentMethodId;

    @NotBlank
    private String paymentType = "STRIPE";
}
