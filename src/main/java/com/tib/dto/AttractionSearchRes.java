package com.tib.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class AttractionSearchRes {
  private String keyword;
  private Page<AttractionListDto> attractions;
}
