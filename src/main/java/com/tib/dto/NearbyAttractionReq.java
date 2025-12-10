package com.tib.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyAttractionReq {
  private BigDecimal latitude;
  private BigDecimal longitude;
  private Double radius;
  private Integer contentTypeId;
  private Integer limit;
}
