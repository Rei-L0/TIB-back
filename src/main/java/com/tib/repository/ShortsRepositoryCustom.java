package com.tib.repository;

import com.tib.dto.ShortsListReq;
import com.tib.entity.Shorts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShortsRepositoryCustom {
  Page<Shorts> findShorts(ShortsListReq req, Pageable pageable);
}
