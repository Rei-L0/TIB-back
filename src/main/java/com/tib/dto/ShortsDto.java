package com.tib.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortsDto {
  private Long id;
  private String title;
  private String thumbnailUrl;
  private String video;
  private Integer good;
  private Integer readcount;
  private Boolean liked;
  private LocalDateTime createdAt;
}
