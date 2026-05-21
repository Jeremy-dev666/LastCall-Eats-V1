package com.lastcalleats.identity.dto;

import lombok.Data;

@Data
public class MerchantProfileRequest {

    private String name;
    private String address;
    private String businessHours;
}
