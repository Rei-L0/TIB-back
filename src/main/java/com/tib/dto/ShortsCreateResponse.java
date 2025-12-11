package com.tib.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortsCreateResponse {

  private Long id;
  private String title;
  private String status;
  private LocalDateTime createdAt;
}
