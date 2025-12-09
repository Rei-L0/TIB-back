package com.tib.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortViewsRes {

  private Long id;
  private Integer readcount;
}
