package com.tib.repository;

import com.tib.entity.ShortsLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortsLikeRepository extends JpaRepository<ShortsLike, Long> {
  Optional<ShortsLike> findByShortsIdAndUserIdentifier(Long shortsId, String userIdentifier);

  boolean existsByShortsIdAndUserIdentifier(Long shortsId, String userIdentifier);

  boolean deleteByShortsIdAndUserIdentifier(Long shortsId, String userIdentifier);
}
