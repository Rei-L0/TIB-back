package com.tib.controller;

import com.tib.dto.ShortPlayEventReq;
import com.tib.dto.ShortPlayEventRes;
import com.tib.dto.ShortViewsRes;
import com.tib.service.ShortService;
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
public class ShortController {

  private final ShortService shortService;

  @PostMapping("/{id}/views")
  public ResponseEntity<ShortViewsRes> increaseViewCount(@PathVariable Long id) {
    return ResponseEntity.ok(shortService.increaseViewCount(id));
  }

  @PostMapping("/{id}/play-events")
  public ResponseEntity<ShortPlayEventRes> createPlayEvent(@PathVariable Long id,
      @RequestBody ShortPlayEventReq req) {
    return ResponseEntity.ok(shortService.createPlayEvent(id, req));
  }
}
