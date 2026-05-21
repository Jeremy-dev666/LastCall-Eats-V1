package com.lastcalleats.community.favorite.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.common.response.PageResult;
import com.lastcalleats.community.favorite.dto.FavoriteResponse;
import com.lastcalleats.community.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/api/user/favorites/{listingId}")
    public ApiResponse<Void> addFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long listingId) {
        favoriteService.addFavorite(userId, listingId);
        return ApiResponse.success();
    }

    @DeleteMapping("/api/user/favorites/{listingId}")
    public ApiResponse<Void> removeFavorite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long listingId) {
        favoriteService.removeFavorite(userId, listingId);
        return ApiResponse.success();
    }

    @GetMapping("/api/user/favorites")
    public ApiResponse<PageResult<FavoriteResponse>> listFavorites(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(PageResult.of(
                favoriteService.listFavorites(userId, PageRequest.of(page - 1, size))));
    }

    @GetMapping("/api/user/favorites/{listingId}")
    public ApiResponse<Boolean> isFavorited(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long listingId) {
        return ApiResponse.success(favoriteService.isFavorited(userId, listingId));
    }
}
