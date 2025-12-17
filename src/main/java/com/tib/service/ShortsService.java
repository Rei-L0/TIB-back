package com.tib.service;

import com.tib.dto.*;
import com.tib.entity.Shorts;
import com.tib.entity.ShortsLike;
import com.tib.entity.ShortsPlayEvent;
import com.tib.entity.ShortsI18n;
import com.tib.entity.AttractionInfo;
import com.tib.entity.Hashtag;
import com.tib.entity.ShortsHashtag;
import com.tib.repository.AttractionRepository;
import com.tib.repository.HashtagRepository;
import com.tib.repository.ShortsHashtagRepository;
import com.tib.repository.ShortsLikeRepository;
import com.tib.repository.ShortsPlayEventRepository;
import com.tib.repository.ShortsRepository;
import com.tib.repository.ShortsI18nRepository;

import lombok.RequiredArgsConstructor;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortsService {

  private final ShortsRepository shortsRepository;
  private final ShortsPlayEventRepository shortsPlayEventRepository;
  private final ShortsLikeRepository shortsLikeRepository;
  private final AttractionRepository attractionRepository;
  private final HashtagRepository hashtagRepository;
  private final ShortsHashtagRepository shortsHashtagRepository;
  private final ShortsI18nRepository shortsI18nRepository;

  private final S3Service s3Service;
  private final ShortsRecommendationService shortsRecommendationService;
  private final TranslationService translationService;

  public ShortsUploadResponse getPreSignedUrl(ShortsUploadRequest request) {
    return s3Service.getPreSignedUrl(request);
  }

  @Value("${cdn.url}")
  private String cdnUrl;  // https://d2n4hxvymniuy1.cloudfront.net

  @Transactional
  public ShortsCreateResponse createShorts(ShortsCreateRequest request) {
    AttractionInfo attractionInfo = attractionRepository.findById(request.getContentId())
            .orElseThrow(() -> new IllegalArgumentException("Attraction not found with contentId: " + request.getContentId()));

    Shorts shorts = Shorts.builder()
            .name(request.getName())
            .title(request.getTitle())
            .video(cdnUrl + "/" + request.getVideoKey())
            .thumbnailUrl(cdnUrl + "/" + request.getThumbnailKey())
            .attractionInfo(attractionInfo)
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .createdAt(java.time.LocalDateTime.now())
            .build();

    shortsRepository.save(shorts);

    if (request.getHashtags() != null) {
      for (String tagName : request.getHashtags()) {
        Hashtag hashtag = hashtagRepository.findByName(tagName)
                .orElseGet(() -> hashtagRepository.save(Hashtag.builder().name(tagName).build()));

        ShortsHashtag shortsHashtag = ShortsHashtag.builder()
                .shorts(shorts)
                .hashtag(hashtag)
                .build();
        shortsHashtagRepository.save(shortsHashtag);
      }
    }

    // 비동기 번역 실행
    translationService.translateShortsAsync(shorts.getId());

    return ShortsCreateResponse.builder()
            .id(shorts.getId())
            .title(shorts.getTitle())
            .status(shorts.getStatus())
            .createdAt(shorts.getCreatedAt())
            .build();
  }

  @Transactional
  public ShortsViewsRes increaseViewCount(Long id) {
    Shorts shorts = shortsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shorts not found with id: " + id));

    shorts.increaseReadCount();
    shortsRepository.save(shorts);

    return ShortsViewsRes.builder()
            .id(shorts.getId())
            .readcount(shorts.getReadcount())
            .build();
  }

  @Transactional
  public ShortsPlayEventRes createPlayEvent(Long shortsId, ShortsPlayEventReq req) {
    Shorts shorts = shortsRepository.findById(shortsId)
            .orElseThrow(() -> new IllegalArgumentException("Shorts not found with id: " + shortsId));

    ShortsPlayEvent event = ShortsPlayEvent.builder()
            .shorts(shorts)
            .userIdentifier(req.getUserIdentifier())
            .watchTimeSec(req.getWatchTimeSec())
            .createdAt(java.time.LocalDateTime.now())
            .build();

    shortsPlayEventRepository.save(event);

    return ShortsPlayEventRes.builder()
            .id(event.getId())
            .shortsId(shorts.getId())
            .userIdentifier(event.getUserIdentifier())
            .watchTimeSec(event.getWatchTimeSec())
            .createdAt(event.getCreatedAt())
            .build();
  }

  @Transactional
  public ShortsLikeResponseDto toggleLike(Long shortsId, String userIdentifier) {
    boolean isLiked = shortsLikeRepository.existsByShortsIdAndUserIdentifier(shortsId, userIdentifier);

    Integer currentGoodCount = shortsRepository.findGoodCountById(shortsId)
            .orElseThrow(() -> new IllegalArgumentException("Shorts not found: " + shortsId));

    if (isLiked) {
      shortsLikeRepository.deleteByShortsIdAndUserIdentifier(shortsId, userIdentifier);
      shortsRepository.decrementGoodCount(shortsId);
      return new ShortsLikeResponseDto(shortsId, false, currentGoodCount - 1);
    } else {
      Shorts shortsRef = shortsRepository.getReferenceById(shortsId);

      ShortsLike newLike = ShortsLike.builder()
              .shorts(shortsRef)
              .userIdentifier(userIdentifier)
              .build();

      shortsLikeRepository.save(newLike);
      shortsRepository.incrementGoodCount(shortsId);
      return new ShortsLikeResponseDto(shortsId, true, currentGoodCount + 1);
    }
  }

  @Transactional(readOnly = true)
  public ShortsListRes getShortsList(ShortsListReq req, String lang) {
    PageRequest pageable = PageRequest.of(req.getPage(), req.getSize());

    Sort sort = Sort.unsorted();
    if (req.getSort() != null) {
      Sort.Direction direction = "asc".equalsIgnoreCase(req.getOrder())
              ? Sort.Direction.ASC
              : Sort.Direction.DESC;

      String property = switch (req.getSort()) {
        case "readcount" -> "readcount";
        case "good" -> "good";
        default -> "createdAt";
      };
      sort = Sort.by(direction, property);
      pageable = PageRequest.of(req.getPage(), req.getSize(), sort);
    }

    Page<Shorts> page = shortsRepository.findShorts(req, pageable);

    Set<Long> likedShortsIds = new HashSet<>();
    if (req.getUserIdentifier() != null && !page.isEmpty()) {
      List<Long> shortsIds = page.getContent().stream().map(Shorts::getId).toList();
      likedShortsIds.addAll(shortsLikeRepository.findLikedShortsIds(req.getUserIdentifier(), shortsIds));
    }

    List<ShortsDto> dtos = page.getContent().stream().map(s -> {
      String title = getTranslatedTitle(s, lang);

      return ShortsDto.builder()
              .id(s.getId())
              .title(title)
              .thumbnailUrl(s.getThumbnailUrl())
              .video(s.getVideo())
              .good(s.getGood())
              .readcount(s.getReadcount())
              .liked(likedShortsIds.contains(s.getId()))
              .createdAt(s.getCreatedAt())
              .latitude(s.getLatitude())
              .longitude(s.getLongitude())
              .radius(req.getRadius())
              .build();
    }).toList();

    return ShortsListRes.builder()
            .content(dtos)
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
  }

  @Transactional(readOnly = true)
  public ShortsDetailDto getShortsDetail(Long id, String userIdentifier, String lang) {
    Shorts shorts = shortsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shorts not found: " + id));

    String title = getTranslatedTitle(shorts, lang);

    boolean liked = false;
    if (userIdentifier != null) {
      liked = shortsLikeRepository.existsByShortsIdAndUserIdentifier(id, userIdentifier);
    }

    return ShortsDetailDto.builder()
            .id(shorts.getId())
            .name(shorts.getName())
            .title(title)
            .video(shorts.getVideo())
            .thumbnailUrl(shorts.getThumbnailUrl())
            .good(shorts.getGood())
            .readcount(shorts.getReadcount())
            .liked(liked)
            .createdAt(shorts.getCreatedAt())
            .latitude(shorts.getLatitude() != null ? shorts.getLatitude().doubleValue() : null)
            .longitude(shorts.getLongitude() != null ? shorts.getLongitude().doubleValue() : null)
            .contentId(shorts.getAttractionInfo() != null ? shorts.getAttractionInfo().getContentId() : null)
            .attractionTitle(shorts.getAttractionInfo() != null ? shorts.getAttractionInfo().getTitle() : null)
            .build();
  }

  @Transactional(readOnly = true)
  public ShortsListRes getRelatedShorts(Long targetId, String userIdentifier, String lang) {
    return shortsRecommendationService.getRelatedShorts(targetId, userIdentifier, lang);
  }

  // 번역된 제목 조회 헬퍼 메서드
  private String getTranslatedTitle(Shorts shorts, String lang) {
    if (lang == null || lang.equals("ko")) {
      return shorts.getTitle();
    }

    return shortsI18nRepository.findByShortsIdAndLang(shorts.getId(), lang)
            .map(ShortsI18n::getTitle)
            .orElse(shorts.getTitle());
  }
}