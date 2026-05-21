package com.lastcalleats.identity.service;

import com.lastcalleats.identity.dto.UserProfileRequest;
import com.lastcalleats.identity.dto.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UserProfileRequest request);

    UserProfileResponse uploadAvatar(Long userId, MultipartFile file);
}
