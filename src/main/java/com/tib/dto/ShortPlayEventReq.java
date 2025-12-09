package com.tib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortPlayEventReq {
  private String userIdentifier;
  private Integer watchTimeSec;
}
