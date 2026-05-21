package com.lastcalleats.marketplace.order.repository;

import com.lastcalleats.marketplace.order.entity.PickupCodeDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PickupCodeRepo extends JpaRepository<PickupCodeDO, Long> {

    Optional<PickupCodeDO> findByOrderId(Long orderId);

    @Query("SELECT p FROM PickupCodeDO p JOIN OrderDO o ON p.orderId = o.id WHERE p.numericCode = :code AND o.merchantId = :merchantId")
    Optional<PickupCodeDO> findByNumericCodeAndMerchantId(@Param("code") String code, @Param("merchantId") Long merchantId);
}
