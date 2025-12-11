package com.tib.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortsDetailDto {
    private Long id;
    private String name;
    private String title;
    private String video;
    private String thumbnailUrl;
    private Integer good;
    private Integer readcount;
    private Boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime recordedAt;
    private Double latitude;
    private Double longitude;

    private Integer contentId;
    private String attractionTitle;
}