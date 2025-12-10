package com.tib.dto;

import com.tib.entity.Gugun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionGugunDto {
  private Integer gugunCode;
  private String gugunName;

  public static RegionGugunDto from(Gugun gugun) {
    return RegionGugunDto.builder()
        .gugunCode(gugun.getGugunCode())
        .gugunName(gugun.getGugunName())
        .build();
  }
}
