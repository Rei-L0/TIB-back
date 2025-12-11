package com.tib.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortsUploadResponse {
  private String videoUploadUrl;
  private String videoKey;
  private String thumbnailUploadUrl;
  private String thumbnailKey;
  private long expiresIn;
}
