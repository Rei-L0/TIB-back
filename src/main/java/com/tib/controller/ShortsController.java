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
  public ResponseEntity<ShortsListRes> getShortsList(
          ShortsListReq req,
          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang
  ) {
    String langCode = lang.split("-")[0];
    return ResponseEntity.ok(shortService.getShortsList(req, langCode));
  }

  @PostMapping("/{id}/views")
  public ResponseEntity<ShortsViewsRes> increaseViewCount(@PathVariable("id") Long id) {
    return ResponseEntity.ok(shortService.increaseViewCount(id));
  }

  @PostMapping("/{id}/play-events")
  public ResponseEntity<ShortsPlayEventRes> createPlayEvent(
          @PathVariable("id") Long id,
          @RequestBody ShortsPlayEventReq req
  ) {
    return ResponseEntity.ok(shortService.createPlayEvent(id, req));
  }

  @PostMapping("/{id}/likes")
  public ResponseEntity<ShortsLikeResponseDto> toggleLike(
          @PathVariable("id") Long id,
          @RequestBody ShortsLikeRequestDto requestDto
  ) {
    return ResponseEntity.ok(shortService.toggleLike(id, requestDto.getUserIdentifier()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ShortsDetailDto> getShortsDetail(
          @PathVariable("id") Long id,
          @RequestParam(value = "userIdentifier", required = false) String userIdentifier,
          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang
  ) {
    String langCode = lang.split("-")[0];
    return ResponseEntity.ok(shortService.getShortsDetail(id, userIdentifier, langCode));
  }

  @PostMapping("/upload-url")
  public ResponseEntity<ShortsUploadResponse> getUploadUrl(@RequestBody ShortsUploadRequest request) {
    return ResponseEntity.ok(shortService.getPreSignedUrl(request));
  }

  @PostMapping
  public ResponseEntity<ShortsCreateResponse> createShorts(@RequestBody ShortsCreateRequest request) {
    return ResponseEntity.ok(shortService.createShorts(request));
  }

  @GetMapping("/{id}/related")
  public ResponseEntity<ShortsListRes> getRelatedShorts(
          @PathVariable("id") Long id,
          @RequestParam(value = "userIdentifier", required = false) String userIdentifier,
          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang
  ) {
    String langCode = lang.split("-")[0];
    return ResponseEntity.ok(shortService.getRelatedShorts(id, userIdentifier, langCode));
  }
}