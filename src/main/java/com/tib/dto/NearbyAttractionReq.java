package com.tib.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearbyAttractionReq {
  private BigDecimal latitude;
  private BigDecimal longitude;
  private Double radius = 5000.0;
  private Integer contentTypeId;
  private Integer limit = 10;
}
