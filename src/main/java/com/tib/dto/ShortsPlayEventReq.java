package com.tib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortsPlayEventReq {
  private String userIdentifier;
  private Integer watchTimeSec;
}
