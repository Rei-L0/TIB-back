package com.tib.service;

import com.tib.dto.ShortsLikeResponseDto;
import com.tib.dto.ShortsPlayEventReq;
import com.tib.dto.ShortsPlayEventRes;
import com.tib.dto.ShortsViewsRes;
import com.tib.entity.Shorts;
import com.tib.entity.ShortsLike;
import com.tib.entity.ShortsPlayEvent;
import com.tib.repository.ShortsLikeRepository;
import com.tib.repository.ShortsPlayEventRepository;
import com.tib.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortsService {

  private final ShortsRepository shortsRepository;
  private final ShortsPlayEventRepository shortsPlayEventRepository;
  private final ShortsLikeRepository shortsLikeRepository;

  @Transactional
  public ShortsViewsRes increaseViewCount(Long id) {
    Shorts shorts = shortsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Shorts not found with id: " + id));

    shorts.increaseReadCount();
    shortsRepository.save(shorts);

    return ShortsViewsRes.builder()
        .id(shorts.getId())
        .readcount(shorts.getReadcount())
        .build();
  }

  @Transactional
  public ShortsPlayEventRes createPlayEvent(Long shortsId, ShortsPlayEventReq req) {
    Shorts shorts = shortsRepository.findById(shortsId)
        .orElseThrow(() -> new IllegalArgumentException("Shorts not found with id: " + shortsId));

    ShortsPlayEvent event = ShortsPlayEvent.builder()
        .shorts(shorts)
        .userIdentifier(req.getUserIdentifier())
        .watchTimeSec(req.getWatchTimeSec())
        .createdAt(java.time.LocalDateTime.now())
        .build();

    shortsPlayEventRepository.save(event);

    return ShortsPlayEventRes.builder()
        .id(event.getId())
        .shortsId(shorts.getId())
        .userIdentifier(event.getUserIdentifier())
        .watchTimeSec(event.getWatchTimeSec())
        .createdAt(event.getCreatedAt())
        .build();
  }

  @Transactional
  public ShortsLikeResponseDto toggleLike(Long shortsId, String userIdentifier) {

    boolean isLiked = shortsLikeRepository.existsByShortsIdAndUserIdentifier(shortsId, userIdentifier);

    Integer currentGoodCount = shortsRepository.findGoodCountById(shortsId)
        .orElseThrow(() -> new IllegalArgumentException("Shorts not found: " + shortsId));

    // [취소 로직]
    if (isLiked) {
      shortsLikeRepository.deleteByShortsIdAndUserIdentifier(shortsId, userIdentifier);

      shortsRepository.decrementGoodCount(shortsId);

      return new ShortsLikeResponseDto(shortsId, false, currentGoodCount - 1);

    }
    // [추가 로직]
    else {
      Shorts shortsRef = shortsRepository.getReferenceById(shortsId);

      ShortsLike newLike = ShortsLike.builder()
          .shorts(shortsRef)
          .userIdentifier(userIdentifier)
          .build();

      shortsLikeRepository.save(newLike);

      shortsRepository.incrementGoodCount(shortsId);

      return new ShortsLikeResponseDto(shortsId, true, currentGoodCount + 1);
    }
  }
}
