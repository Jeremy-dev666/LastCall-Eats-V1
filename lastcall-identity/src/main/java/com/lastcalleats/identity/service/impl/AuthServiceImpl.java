package com.lastcalleats.identity.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.identity.dto.*;
import com.lastcalleats.identity.entity.MerchantDO;
import com.lastcalleats.identity.entity.UserDO;
import com.lastcalleats.identity.repository.MerchantRepo;
import com.lastcalleats.identity.repository.UserRepo;
import com.lastcalleats.identity.security.JwtUtil;
import com.lastcalleats.identity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final MerchantRepo merchantRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse registerUser(UserRegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserDO user = UserDO.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), "USER");
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role("USER")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerMerchant(MerchantRegisterRequest request) {
        if (merchantRepo.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        MerchantDO merchant = MerchantDO.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .address(request.getAddress())
                .businessHours(request.getBusinessHours())
                .build();
        merchantRepo.save(merchant);

        String token = jwtUtil.generateToken(merchant.getId(), merchant.getEmail(), "MERCHANT");
        return AuthResponse.builder()
                .token(token)
                .userId(merchant.getId())
                .email(merchant.getEmail())
                .role("MERCHANT")
                .build();
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        UserDO user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), "USER");
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role("USER")
                .build();
    }

    @Override
    public AuthResponse loginMerchant(LoginRequest request) {
        MerchantDO merchant = merchantRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), merchant.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(merchant.getId(), merchant.getEmail(), "MERCHANT");
        return AuthResponse.builder()
                .token(token)
                .userId(merchant.getId())
                .email(merchant.getEmail())
                .role("MERCHANT")
                .build();
    }
}
