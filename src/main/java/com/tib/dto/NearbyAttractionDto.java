package com.tib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyAttractionDto {
  private Integer contentId;
  private String title;
  private String sidoName;
  private String gugunName;
  private String contentTypeName;
  private String overview;
  private String firstImage;
  private Double distance;
  private Long shortsCount;
}
