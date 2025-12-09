package com.tib.service;

import com.tib.dto.ShortViewsRes;
import com.tib.entity.Short;
import com.tib.repository.ShortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortService {

  private final ShortRepository shortRepository;

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
}
