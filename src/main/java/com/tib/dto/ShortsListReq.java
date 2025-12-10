package com.tib.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShortsListReq {
  private Integer contentId;
  private Integer sidoCode;
  private Integer gugunCode;
  private Integer contentTypeId;
  private String hashtag;
  private String weather;
  private String theme;
  private String season;
  private String status;
  private String lang;

  // Pagination
  @Builder.Default
  private int page = 1;
  @Builder.Default
  private int size = 20;

  // Sort
  @Builder.Default
  private String sort = "id";
  @Builder.Default
  private String order = "desc";

  // User
  private String userIdentifier;
}
