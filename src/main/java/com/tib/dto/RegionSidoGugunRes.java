package com.tib.dto;

import com.tib.entity.Sido;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionSidoGugunRes {
  private Integer sidoCode;
  private String sidoName;
  private List<RegionGugunDto> guguns;

  public static RegionSidoGugunRes of(Sido sido, List<RegionGugunDto> guguns) {
    return RegionSidoGugunRes.builder()
        .sidoCode(sido.getSidoCode())
        .sidoName(sido.getSidoName())
        .guguns(guguns)
        .build();
  }
}
