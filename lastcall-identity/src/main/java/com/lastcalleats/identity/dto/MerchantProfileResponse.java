package com.lastcalleats.identity.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MerchantProfileResponse {

    private Long id;
    private String email;
    private String name;
    private String address;
    private String businessHours;
    private Boolean isActive;
}
