package com.tib.controller;

import com.tib.dto.*;
import com.tib.service.ShortsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorts")
@RequiredArgsConstructor
public class ShortsController {

  private final ShortsService shortService;

  @GetMapping
  public ResponseEntity<ShortsListRes> getShortsList(ShortsListReq req) {
    return ResponseEntity.ok(shortService.getShortsList(req));
  }

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

  @GetMapping("/{id}")
  public ResponseEntity<ShortsDetailDto> getShortsDetail(
      @PathVariable Long id,
      @RequestParam(required = false) String userIdentifier) {
    return ResponseEntity.ok(shortService.getShortsDetail(id, userIdentifier));
  }

  @PostMapping("/upload-url")
  public ResponseEntity<ShortsUploadResponse> getUploadUrl(@RequestBody ShortsUploadRequest request) {
    return ResponseEntity.ok(shortService.getPreSignedUrl(request));
  }
}
