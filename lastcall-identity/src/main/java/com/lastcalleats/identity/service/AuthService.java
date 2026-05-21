package com.lastcalleats.identity.service;

import com.lastcalleats.identity.dto.*;

public interface AuthService {

    AuthResponse registerUser(UserRegisterRequest request);

    AuthResponse registerMerchant(MerchantRegisterRequest request);

    AuthResponse loginUser(LoginRequest request);

    AuthResponse loginMerchant(LoginRequest request);
}
