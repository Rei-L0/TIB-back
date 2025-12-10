package com.tib.controller;

import com.tib.dto.NearbyAttractionReq;
import com.tib.dto.NearbyAttractionRes;
import com.tib.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
