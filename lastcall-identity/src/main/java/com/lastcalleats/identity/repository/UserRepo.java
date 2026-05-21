package com.lastcalleats.identity.repository;

import com.lastcalleats.identity.entity.UserDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserDO, Long> {

    Optional<UserDO> findByEmail(String email);

    boolean existsByEmail(String email);
}
