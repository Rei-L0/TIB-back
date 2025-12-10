package com.tib.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tib.dto.AttractionSearchRes;
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
}
