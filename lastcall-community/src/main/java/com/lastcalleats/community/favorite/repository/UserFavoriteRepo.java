package com.lastcalleats.community.favorite.repository;

import com.lastcalleats.community.favorite.entity.UserFavoriteDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFavoriteRepo extends JpaRepository<UserFavoriteDO, Long> {

    Page<UserFavoriteDO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<UserFavoriteDO> findByUserIdAndListingId(Long userId, Long listingId);

    boolean existsByUserIdAndListingId(Long userId, Long listingId);

    void deleteByUserIdAndListingId(Long userId, Long listingId);
}
