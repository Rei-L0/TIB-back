package com.tib.repository;

import com.tib.entity.Shorts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {

  @Query("SELECT s.good FROM Shorts s WHERE s.id = :id")
  Optional<Integer> findGoodCountById(@Param("id") Long id);

  // 좋아요 수 증가
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Shorts s SET s.good = s.good + 1 WHERE s.id = :id")
  void incrementGoodCount(@Param("id") Long id);

  // 좋아요 수 감소
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Shorts s SET s.good = s.good - 1 WHERE s.id = :id AND s.good > 0")
  void decrementGoodCount(@Param("id") Long id);
}
