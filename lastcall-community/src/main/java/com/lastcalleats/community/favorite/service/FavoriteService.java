package com.lastcalleats.community.favorite.service;

import com.lastcalleats.community.favorite.dto.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    void addFavorite(Long userId, Long listingId);

    void removeFavorite(Long userId, Long listingId);

    Page<FavoriteResponse> listFavorites(Long userId, Pageable pageable);

    boolean isFavorited(Long userId, Long listingId);
}
