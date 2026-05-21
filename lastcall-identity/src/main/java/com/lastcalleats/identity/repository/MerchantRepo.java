package com.lastcalleats.identity.repository;

import com.lastcalleats.identity.entity.MerchantDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepo extends JpaRepository<MerchantDO, Long> {

    Optional<MerchantDO> findByEmail(String email);

    boolean existsByEmail(String email);
}
