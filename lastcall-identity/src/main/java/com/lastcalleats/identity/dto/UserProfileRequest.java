package com.lastcalleats.identity.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequest {

    @Size(max = 100, message = "Nickname cannot exceed 100 characters")
    private String nickname;
}
