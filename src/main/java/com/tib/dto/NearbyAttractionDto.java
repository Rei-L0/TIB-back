package com.tib.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class NearbyAttractionDto {
  private Integer contentId;
  private String title;
  private String sidoName;
  private String gugunName;
  private String overview;
  private String firstImage;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private Double distance;
  private Long shortsCount;

  public NearbyAttractionDto(Integer contentId, String title, String sidoName, String gugunName,
                             String overview, String firstImage, BigDecimal latitude, BigDecimal longitude,
                             Number distance, Number shortsCount) {
    this.contentId = contentId;
    this.title = title;
    this.sidoName = sidoName;
    this.gugunName = gugunName;
    this.overview = overview;
    this.firstImage = firstImage;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distance = distance != null ? distance.doubleValue() : null;
    this.shortsCount = shortsCount != null ? shortsCount.longValue() : null;
  }
}