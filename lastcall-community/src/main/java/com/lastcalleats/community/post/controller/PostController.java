package com.lastcalleats.community.post.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.common.response.PageResult;
import com.lastcalleats.community.post.dto.CreatePostRequest;
import com.lastcalleats.community.post.dto.PostResponse;
import com.lastcalleats.community.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/posts")
    public ApiResponse<PostResponse> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        return ApiResponse.success(postService.createPost(userId, request));
    }

    @GetMapping("/api/posts")
    public ApiResponse<PageResult<PostResponse>> listAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResult.of(
                postService.listAllPosts(PageRequest.of(page - 1, size))));
    }

    @GetMapping("/api/posts/user/{userId}")
    public ApiResponse<PageResult<PostResponse>> listPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResult.of(
                postService.listPostsByUser(userId, PageRequest.of(page - 1, size))));
    }

    @GetMapping("/api/posts/merchant/{merchantId}")
    public ApiResponse<PageResult<PostResponse>> listPostsByMerchant(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResult.of(
                postService.listPostsByMerchant(merchantId, PageRequest.of(page - 1, size))));
    }

    @GetMapping("/api/posts/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
        return ApiResponse.success(postService.getPost(postId));
    }

    @DeleteMapping("/api/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        postService.deletePost(userId, postId);
        return ApiResponse.success();
    }
}
