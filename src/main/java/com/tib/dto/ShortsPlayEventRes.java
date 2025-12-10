package com.tib.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortsPlayEventRes {
  private Long id;
  private Long shortsId;
  private String userIdentifier;
  private Integer watchTimeSec;
  private LocalDateTime createdAt;
}
