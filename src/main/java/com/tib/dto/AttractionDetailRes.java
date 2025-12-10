package com.tib.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttractionDetailRes {
  private Integer contentId;
  private String title;
  private String addr1;
  private String addr2;
  private String zipcode;
  private String tel;
  private String firstImage;
  private String sidoName;
  private String gugunName;
  private Long shortsCount;
  private BigDecimal latitude;
  private BigDecimal longitude;

  private Detail detail;
  private Description description;

  @Getter
  @Builder
  public static class Detail {
    private String cat1;
    private String cat2;
    private String cat3;
    private String createdTime;
    private String modifiedTime;
    private String booktour;
  }

  @Getter
  @Builder
  public static class Description {
    private String homepage;
    private String overview;
    private String telname;
  }
}
