package com.tib.repository;

import com.tib.dto.ShortsListReq;
import com.tib.entity.Shorts;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShortsRepositoryCustom {
  Page<Shorts> findShorts(ShortsListReq req, Pageable pageable);

  List<Shorts> findCandidates(Long targetId, Integer gugunCode, String theme, int limit);
}
