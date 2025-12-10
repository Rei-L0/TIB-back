package com.tib.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttractionListDto {
  private Integer contentId;
  private String title;
  private String firstImage;
  private String addr1;
  private String tel;
  private Integer readcount;
  private String sidoName;
  private String gugunName;
}
