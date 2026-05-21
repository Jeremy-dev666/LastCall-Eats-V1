package com.lastcalleats.community.favorite.service.impl;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.util.Assert;
import com.lastcalleats.community.favorite.dto.FavoriteResponse;
import com.lastcalleats.community.favorite.entity.UserFavoriteDO;
import com.lastcalleats.community.favorite.repository.UserFavoriteRepo;
import com.lastcalleats.community.favorite.service.FavoriteService;
import com.lastcalleats.identity.entity.MerchantDO;
import com.lastcalleats.identity.repository.MerchantRepo;
import com.lastcalleats.marketplace.product.entity.ProductListingDO;
import com.lastcalleats.marketplace.product.entity.ProductTemplateDO;
import com.lastcalleats.marketplace.product.repository.ProductListingRepo;
import com.lastcalleats.marketplace.product.repository.ProductTemplateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserFavoriteRepo userFavoriteRepo;
    private final ProductListingRepo productListingRepo;
    private final ProductTemplateRepo productTemplateRepo;
    private final MerchantRepo merchantRepo;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long listingId) {
        Assert.isTrue(!userFavoriteRepo.existsByUserIdAndListingId(userId, listingId),
                ErrorCode.FAVORITE_ALREADY_EXISTS);

        productListingRepo.findById(listingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LISTING_NOT_FOUND));

        UserFavoriteDO favorite = UserFavoriteDO.builder()
                .userId(userId)
                .listingId(listingId)
                .build();
        userFavoriteRepo.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long listingId) {
        userFavoriteRepo.deleteByUserIdAndListingId(userId, listingId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> listFavorites(Long userId, Pageable pageable) {
        return userFavoriteRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(Long userId, Long listingId) {
        return userFavoriteRepo.existsByUserIdAndListingId(userId, listingId);
    }

    private FavoriteResponse toResponse(UserFavoriteDO favorite) {
        ProductListingDO listing = productListingRepo.findById(favorite.getListingId()).orElse(null);
        if (listing == null) {
            return FavoriteResponse.builder()
                    .listingId(favorite.getListingId())
                    .favoritedAt(favorite.getCreatedAt())
                    .build();
        }

        ProductTemplateDO template = productTemplateRepo.findById(listing.getTemplateId()).orElse(null);
        String templateName = template != null ? template.getName() : null;
        java.math.BigDecimal originalPrice = template != null ? template.getOriginalPrice() : null;

        MerchantDO merchant = merchantRepo.findById(listing.getMerchantId()).orElse(null);
        String merchantName = merchant != null ? merchant.getName() : null;

        return FavoriteResponse.builder()
                .listingId(favorite.getListingId())
                .templateName(templateName)
                .merchantName(merchantName)
                .discountPrice(listing.getDiscountPrice())
                .originalPrice(originalPrice)
                .pickupStart(listing.getPickupStart())
                .pickupEnd(listing.getPickupEnd())
                .date(listing.getDate())
                .favoritedAt(favorite.getCreatedAt())
                .build();
    }
}
