package com.tib.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tib.dto.ShortsLikeResponseDto;
import com.tib.entity.Shorts;
import com.tib.repository.ShortsLikeRepository;
import com.tib.repository.ShortsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ShortsLikeServiceTest {

  @Autowired
  private ShortsService shortsService;

  @Autowired
  private ShortsRepository shortsRepository;

  @Autowired
  private ShortsLikeRepository shortsLikeRepository;

  @Test
  @DisplayName("Shorts Like Toggle Test: Like -> Cancel Like -> Like")
  void toggleLikeTest() {
    // Given
    Long shortsId = 3L;
    String userIdentifier = "test-new-user";

    // Check initial state (optional, assuming data.sql is loaded)
    // Short 3 has good=50 from data.sql

    // When & Then 1: First Like (Should Increase)
    ShortsLikeResponseDto res1 = shortsService.toggleLike(shortsId, userIdentifier);
    assertThat(res1.isLiked()).isTrue();
    // good starts at 50, so +1 = 51
    assertThat(res1.getGoodCount()).isEqualTo(51);
    assertThat(shortsLikeRepository.existsByShortsIdAndUserIdentifier(shortsId, userIdentifier)).isTrue();

    // When & Then 2: Cancel Like (Should Decrease)
    ShortsLikeResponseDto res2 = shortsService.toggleLike(shortsId, userIdentifier);
    assertThat(res2.isLiked()).isFalse();
    assertThat(res2.getGoodCount()).isEqualTo(50);
    assertThat(shortsLikeRepository.existsByShortsIdAndUserIdentifier(shortsId, userIdentifier)).isFalse();

    // When & Then 3: Second Like (Should Increase Again)
    ShortsLikeResponseDto res3 = shortsService.toggleLike(shortsId, userIdentifier);
    assertThat(res3.isLiked()).isTrue();
    assertThat(res3.getGoodCount()).isEqualTo(51);
    assertThat(shortsLikeRepository.existsByShortsIdAndUserIdentifier(shortsId, userIdentifier)).isTrue();
  }
}
