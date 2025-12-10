package com.tib.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HealthController {

  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    log.info("Health check");
    return ResponseEntity.ok("Server is running");
  }
}
