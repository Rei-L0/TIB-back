package com.tib.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tib.dto.AttractionSearchRes;
import com.tib.dto.NearbyAttractionDto;
import com.tib.dto.NearbyAttractionReq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AttractionServiceTest {

  @Autowired
  private AttractionService attractionService;

  @Test
  @DisplayName("Search Attractions by Keyword")
  void searchAttractionsTest() {
    // Given
    // Using "해운대" which exists in data.sql (contentId 1001)
    String keyword = "해운대";
    PageRequest pageable = PageRequest.of(0, 10);

    // When
    AttractionSearchRes result = attractionService.searchAttractions(keyword, pageable);

    // Then
    assertThat(result.getKeyword()).isEqualTo(keyword);
    assertThat(result.getAttractions().getTotalElements()).isGreaterThanOrEqualTo(1);
    assertThat(result.getAttractions().getContent().get(0).getTitle()).contains(keyword);

    // Check fields are mapped
    assertThat(result.getAttractions().getContent().get(0).getContentId()).isEqualTo(1001);
    assertThat(result.getAttractions().getContent().get(0).getSidoName()).isEqualTo("부산");
  }

  @Test
  @DisplayName("Get Nearby Attractions")
  void getNearbyAttractionsTest() {
    // Given
    // Haedong Yonggungsa (approx lat: 35.188, long: 129.223)
    // Using simple coordinates close to existing data
    java.math.BigDecimal lat = new java.math.BigDecimal("35.15");
    java.math.BigDecimal lon = new java.math.BigDecimal("129.15");

    NearbyAttractionReq req = NearbyAttractionReq.builder()
        .latitude(lat)
        .longitude(lon)
        .radius((double) 5000)
        .limit(10)
        .build();

    // When
    com.tib.dto.NearbyAttractionRes res = attractionService.getNearbyAttractions(req);

    // Then
    assertThat(res.getAttractions()).isNotEmpty();
    assertThat(res.getAttractions().get(0).getTitle()).isNotNull();
    assertThat(res.getAttractions().get(0).getDistance()).isNotNull();
  }
}
