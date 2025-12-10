package com.tib.controller;

import com.tib.dto.RegionSidoGugunRes;
import com.tib.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

  private final RegionService regionService;

  @GetMapping("/sidos/{sidoCode}/guguns")
  public ResponseEntity<RegionSidoGugunRes> getGugunsBySidoCode(@PathVariable Integer sidoCode) {
    RegionSidoGugunRes response = regionService.getGugunsBySidoCode(sidoCode);
    return ResponseEntity.ok(response);
  }
}
