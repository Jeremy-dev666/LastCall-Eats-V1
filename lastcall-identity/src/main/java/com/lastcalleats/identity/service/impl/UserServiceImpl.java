package com.lastcalleats.identity.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.storage.StorageStrategy;
import com.lastcalleats.identity.dto.UserProfileRequest;
import com.lastcalleats.identity.dto.UserProfileResponse;
import com.lastcalleats.identity.entity.UserDO;
import com.lastcalleats.identity.repository.UserRepo;
import com.lastcalleats.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final StorageStrategy storageStrategy;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        UserDO user = findUser(userId);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileRequest request) {
        UserDO user = findUser(userId);
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        userRepo.save(user);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse uploadAvatar(Long userId, MultipartFile file) {
        UserDO user = findUser(userId);
        String url = storageStrategy.upload(file, "avatars");
        user.setAvatarUrl(url);
        userRepo.save(user);
        return toResponse(user);
    }

    private UserDO findUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private UserProfileResponse toResponse(UserDO user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
