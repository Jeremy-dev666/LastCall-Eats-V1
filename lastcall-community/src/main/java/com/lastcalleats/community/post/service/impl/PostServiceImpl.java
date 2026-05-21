package com.lastcalleats.community.post.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.util.Assert;
import com.lastcalleats.community.post.dto.CreatePostRequest;
import com.lastcalleats.community.post.dto.PostResponse;
import com.lastcalleats.community.post.entity.PostDO;
import com.lastcalleats.community.post.repository.PostRepo;
import com.lastcalleats.community.post.service.PostService;
import com.lastcalleats.identity.entity.MerchantDO;
import com.lastcalleats.identity.entity.UserDO;
import com.lastcalleats.identity.repository.MerchantRepo;
import com.lastcalleats.identity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final MerchantRepo merchantRepo;

    @Override
    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        PostDO post = PostDO.builder()
                .userId(userId)
                .merchantId(request.getMerchantId())
                .content(request.getContent())
                .imageUrls(request.getImageUrls())
                .build();
        postRepo.save(post);
        return toResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listAllPosts(Pageable pageable) {
        return postRepo.findByIsVisibleTrueOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPostsByUser(Long userId, Pageable pageable) {
        return postRepo.findByUserIdAndIsVisibleTrueOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> listPostsByMerchant(Long merchantId, Pageable pageable) {
        return postRepo.findByMerchantIdAndIsVisibleTrueOrderByCreatedAtDesc(merchantId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        PostDO post = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return toResponse(post);
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        PostDO post = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Assert.equals(post.getUserId(), userId, ErrorCode.POST_FORBIDDEN);
        postRepo.delete(post);
    }

    private PostResponse toResponse(PostDO post) {
        UserDO user = userRepo.findById(post.getUserId()).orElse(null);
        String userNickname = user != null ? user.getNickname() : null;
        String userAvatarUrl = user != null ? user.getAvatarUrl() : null;

        String merchantName = null;
        if (post.getMerchantId() != null) {
            MerchantDO merchant = merchantRepo.findById(post.getMerchantId()).orElse(null);
            merchantName = merchant != null ? merchant.getName() : null;
        }

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .userNickname(userNickname)
                .userAvatarUrl(userAvatarUrl)
                .merchantId(post.getMerchantId())
                .merchantName(merchantName)
                .content(post.getContent())
                .imageUrls(post.getImageUrls())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
