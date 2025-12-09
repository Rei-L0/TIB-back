package com.tib.repository;

import com.tib.entity.ShortLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortLikeRepository extends JpaRepository<ShortLike, Long> {
  Optional<ShortLike> findByShortIdAndUserIdentifier(Long shortsId, String userIdentifier);

  boolean existsByShortIdAndUserIdentifier(Long shortsId, String userIdentifier);

  boolean deleteByShortIdAndUserIdentifier(Long shortsId, String userIdentifier);
}
