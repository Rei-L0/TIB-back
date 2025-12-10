package com.tib.controller;

import com.tib.dto.ShortsPlayEventReq;
import com.tib.dto.ShortsPlayEventRes;
import com.tib.dto.ShortsViewsRes;
import com.tib.dto.ShortsLikeRequestDto;
import com.tib.dto.ShortsLikeResponseDto;
import com.tib.service.ShortsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shorts")
@RequiredArgsConstructor
public class ShortsController {

  private final ShortsService shortService;

  @PostMapping("/{id}/views")
  public ResponseEntity<ShortsViewsRes> increaseViewCount(@PathVariable Long id) {
    return ResponseEntity.ok(shortService.increaseViewCount(id));
  }

  @PostMapping("/{id}/play-events")
  public ResponseEntity<ShortsPlayEventRes> createPlayEvent(@PathVariable Long id,
      @RequestBody ShortsPlayEventReq req) {
    return ResponseEntity.ok(shortService.createPlayEvent(id, req));
  }

  @PostMapping("/{id}/likes")
  public ResponseEntity<ShortsLikeResponseDto> toggleLike(
      @PathVariable Long id,
      @RequestBody ShortsLikeRequestDto requestDto) {

    ShortsLikeResponseDto response = shortService.toggleLike(id, requestDto.getUserIdentifier());

    return ResponseEntity.ok(response);
  }
}
