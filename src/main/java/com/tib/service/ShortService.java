package com.tib.service;

import com.tib.dto.ShortLikeResponseDto;
import com.tib.dto.ShortPlayEventReq;
import com.tib.dto.ShortPlayEventRes;
import com.tib.dto.ShortViewsRes;
import com.tib.entity.Short;
import com.tib.entity.ShortLike;
import com.tib.entity.ShortPlayEvent;
import com.tib.repository.ShortLikeRepository;
import com.tib.repository.ShortPlayEventRepository;
import com.tib.repository.ShortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortService {

  private final ShortRepository shortRepository;
  private final ShortPlayEventRepository shortPlayEventRepository;
  private final ShortLikeRepository shortLikeRepository;

  @Transactional
  public ShortViewsRes increaseViewCount(Long id) {
    Short shorts = shortRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Short not found with id: " + id));

    shorts.increaseReadCount();
    shortRepository.save(shorts);

    return ShortViewsRes.builder()
        .id(shorts.getId())
        .readcount(shorts.getReadcount())
        .build();
  }

  @Transactional
  public ShortPlayEventRes createPlayEvent(Long shortsId, ShortPlayEventReq req) {
    Short shorts = shortRepository.findById(shortsId)
        .orElseThrow(() -> new IllegalArgumentException("Short not found with id: " + shortsId));

    ShortPlayEvent event = ShortPlayEvent.builder()
        .shorts(shorts)
        .userIdentifier(req.getUserIdentifier())
        .watchTimeSec(req.getWatchTimeSec())
        .createdAt(java.time.LocalDateTime.now())
        .build();

    shortPlayEventRepository.save(event);

    return ShortPlayEventRes.builder()
        .id(event.getId())
        .shortsId(shorts.getId())
        .userIdentifier(event.getUserIdentifier())
        .watchTimeSec(event.getWatchTimeSec())
        .createdAt(event.getCreatedAt())
        .build();
  }

  @Transactional
  public ShortLikeResponseDto toggleLike(Long shortsId, String userIdentifier) {

    boolean isLiked = shortLikeRepository.existsByShortIdAndUserIdentifier(shortsId, userIdentifier);

    Integer currentGoodCount = shortRepository.findGoodCountById(shortsId)
        .orElseThrow(() -> new IllegalArgumentException("Short not found: " + shortsId));

    // [취소 로직]
    if (isLiked) {
      shortLikeRepository.deleteByShortIdAndUserIdentifier(shortsId, userIdentifier);

      shortRepository.decrementGoodCount(shortsId);

      return new ShortLikeResponseDto(shortsId, false, currentGoodCount - 1);

    }
    // [추가 로직]
    else {
      Short shortsRef = shortRepository.getReferenceById(shortsId);

      ShortLike newLike = ShortLike.builder()
          .shorts(shortsRef)
          .userIdentifier(userIdentifier)
          .build();

      shortLikeRepository.save(newLike);

      shortRepository.incrementGoodCount(shortsId);

      return new ShortLikeResponseDto(shortsId, true, currentGoodCount + 1);
    }
  }
}
