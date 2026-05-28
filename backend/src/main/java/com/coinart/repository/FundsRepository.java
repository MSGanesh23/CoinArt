package com.coinart.repository;

import com.coinart.entity.Funds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FundsRepository extends JpaRepository<Funds, Long> {
    Optional<Funds> findByUserId(Long userId);
}
