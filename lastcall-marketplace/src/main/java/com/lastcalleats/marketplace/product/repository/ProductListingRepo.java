package com.lastcalleats.marketplace.product.repository;

import com.lastcalleats.marketplace.product.entity.ProductListingDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductListingRepo extends JpaRepository<ProductListingDO, Long> {

    Page<ProductListingDO> findByIsAvailableTrueAndDateOrderByCreatedAtDesc(LocalDate date, Pageable pageable);

    List<ProductListingDO> findByMerchantIdOrderByCreatedAtDesc(Long merchantId);

    int countByMerchantIdAndIsAvailableTrue(Long merchantId);
}
