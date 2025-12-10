package com.tib.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NearbyAttractionRes {
  private Center center;
  private Double radius;
  private List<NearbyAttractionDto> attractions;

  @Getter
  @Builder
  public static class Center {
    private BigDecimal latitude;
    private BigDecimal longitude;
  }
}
