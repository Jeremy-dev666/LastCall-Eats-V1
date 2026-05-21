package com.lastcalleats.community.post.service;

import com.lastcalleats.community.post.dto.CreatePostRequest;
import com.lastcalleats.community.post.dto.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostResponse createPost(Long userId, CreatePostRequest request);

    Page<PostResponse> listAllPosts(Pageable pageable);

    Page<PostResponse> listPostsByUser(Long userId, Pageable pageable);

    Page<PostResponse> listPostsByMerchant(Long merchantId, Pageable pageable);

    PostResponse getPost(Long postId);

    void deletePost(Long userId, Long postId);
}
