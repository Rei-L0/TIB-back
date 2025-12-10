package com.tib.repository;

import com.tib.entity.ShortsLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShortsLikeRepository extends JpaRepository<ShortsLike, Long> {

  // Check if a user likes a specific short
  boolean existsByShortsIdAndUserIdentifier(Long shortsId, String userIdentifier);

  // Delete like
  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM ShortsLike sl WHERE sl.shorts.id = :shortsId AND sl.userIdentifier = :userIdentifier")
  void deleteByShortsIdAndUserIdentifier(@Param("shortsId") Long shortsId,
      @Param("userIdentifier") String userIdentifier);

  @Query("SELECT sl.shorts.id FROM ShortsLike sl WHERE sl.userIdentifier = :userIdentifier AND sl.shorts.id IN :shortsIds")
  List<Long> findLikedShortsIds(@Param("userIdentifier") String userIdentifier,
      @Param("shortsIds") List<Long> shortsIds);
}
