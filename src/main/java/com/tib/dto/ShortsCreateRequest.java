package com.tib.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortsCreateRequest {

  private String videoKey;
  private String thumbnailKey;
  private String name;
  private String title;
  private Integer contentId;
  private String weather;
  private String theme;
  private String season;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private List<String> hashtags;
}
