package com.lastcalleats.marketplace.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeResponse {

    private Long orderId;
    private String customerNickname;
    private String productName;
    private boolean success;
}
