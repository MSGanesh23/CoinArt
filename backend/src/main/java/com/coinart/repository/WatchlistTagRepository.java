package com.coinart.repository;

import com.coinart.entity.WatchlistTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistTagRepository extends JpaRepository<WatchlistTag, Long> {
    List<WatchlistTag> findByUserIdOrderByNameAsc(Long userId);

    Optional<WatchlistTag> findByUserIdAndId(Long userId, Long tagId);

    boolean existsByUserIdAndName(Long userId, String name);

    void deleteByUserIdAndId(Long userId, Long tagId);
}
