package com.lastcalleats.community.review.repository;

import com.lastcalleats.community.review.entity.ReviewDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewDO, Long> {

    Optional<ReviewDO> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    Page<ReviewDO> findByMerchantIdAndIsVisibleTrueOrderByCreatedAtDesc(Long merchantId, Pageable pageable);

    Page<ReviewDO> findByTemplateIdAndIsVisibleTrueOrderByCreatedAtDesc(Long templateId, Pageable pageable);
}
