package com.tib.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortsListRes {
  private List<ShortsDto> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
}
