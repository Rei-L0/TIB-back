package com.tib.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShortsUploadRequest {
  private String videoFileName;
  private String videoContentType;
  private long videoFileSize;
  private String thumbnailFileName;
  private String thumbnailContentType;
  private long thumbnailFileSize;
}
