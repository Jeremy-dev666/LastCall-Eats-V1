package com.lastcalleats.marketplace.order.repository;

import com.lastcalleats.marketplace.order.entity.OrderDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<OrderDO, Long> {

    List<OrderDO> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<OrderDO> findByMerchantIdOrderByCreatedAtDesc(Long merchantId);

    boolean existsByUserIdAndListingIdAndStatusNot(Long userId, Long listingId, String status);

    @Query("SELECT COUNT(o) FROM OrderDO o WHERE o.merchantId = :merchantId AND FUNCTION('DATE', o.createdAt) = CURRENT_DATE")
    int countTodayOrdersByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT COALESCE(SUM(o.price), 0) FROM OrderDO o WHERE o.merchantId = :merchantId AND o.status IN ('PAID', 'COMPLETED') AND FUNCTION('DATE', o.createdAt) = CURRENT_DATE")
    BigDecimal sumTodayRevenueByMerchantId(@Param("merchantId") Long merchantId);
}
