package com.tib.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class ShortLikeResponseDto {
  private Long shortsId;
  private boolean liked;
  private long goodCount;
}