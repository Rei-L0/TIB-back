package com.tib.controller;

import com.tib.dto.AttractionDetailRes;
import com.tib.dto.AttractionSearchRes;
import com.tib.dto.NearbyAttractionReq;
import com.tib.dto.NearbyAttractionRes;
import com.tib.service.AttractionService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attractions")
@RequiredArgsConstructor
public class AttractionController {

  private final AttractionService attractionService;

  @GetMapping("/nearby")
  public ResponseEntity<NearbyAttractionRes> getNearbyAttractions(@ModelAttribute NearbyAttractionReq req) {
    return ResponseEntity.ok(attractionService.getNearbyAttractions(req));
  }

  @GetMapping("/{contentId}")
  public ResponseEntity<AttractionDetailRes> getAttractionDetail(
      @PathVariable Integer contentId) {
    return ResponseEntity.ok(attractionService.getAttractionDetail(contentId));
  }

  @GetMapping("/search")
  public ResponseEntity<AttractionSearchRes> searchAttractions(
      @RequestParam String keyword,
      Pageable pageable) {
    return ResponseEntity.ok(attractionService.searchAttractions(keyword, pageable));
  }
}
