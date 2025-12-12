package com.tib.service;

import com.tib.dto.*;
import com.tib.entity.Shorts;
import com.tib.entity.ShortsLike;
import com.tib.entity.ShortsPlayEvent;
import com.tib.entity.AttractionInfo;
import com.tib.entity.Hashtag;
import com.tib.entity.ShortsHashtag;
import com.tib.repository.AttractionRepository;
import com.tib.repository.HashtagRepository;
import com.tib.repository.ShortsHashtagRepository;
import com.tib.repository.ShortsLikeRepository;
import com.tib.repository.ShortsPlayEventRepository;
import com.tib.repository.ShortsRepository;

import lombok.RequiredArgsConstructor;

import java.util.*;

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

  private final S3Service s3Service;
  private final ShortsRecommendationService shortsRecommendationService;

  public ShortsUploadResponse getPreSignedUrl(ShortsUploadRequest request) {
    return s3Service.getPreSignedUrl(request);
  }

  @Transactional
  public ShortsCreateResponse createShorts(ShortsCreateRequest request) {
    AttractionInfo attractionInfo = attractionRepository.findById(request.getContentId())
        .orElseThrow(
            () -> new IllegalArgumentException("Attraction not found with contentId: " + request.getContentId()));

    Shorts shorts = Shorts.builder()
        .name(request.getName())
        .title(request.getTitle())
        .video(String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", s3Service.getBucket(),
            request.getVideoKey()))
        .thumbnailUrl(String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", s3Service.getBucket(),
            request.getThumbnailKey()))
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

    // [취소 로직]
    if (isLiked) {
      shortsLikeRepository.deleteByShortsIdAndUserIdentifier(shortsId, userIdentifier);

      shortsRepository.decrementGoodCount(shortsId);

      return new ShortsLikeResponseDto(shortsId, false, currentGoodCount - 1);

    }
    // [추가 로직]
    else {
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
  public ShortsListRes getShortsList(ShortsListReq req) {
    PageRequest pageable = PageRequest.of(
        req.getPage(),
        req.getSize());

    Sort sort = Sort.unsorted();
    if (req.getSort() != null) {
      Sort.Direction direction = "asc".equalsIgnoreCase(req.getOrder())
          ? Sort.Direction.ASC
          : Sort.Direction.DESC;

      String property = "createdAt";
      switch (req.getSort()) {
        case "readcount":
          property = "readcount";
          break;
        case "good":
          property = "good";
          break;
        case "createdAt":
          property = "createdAt";
          break;
        default:
          property = "createdAt";
          break;
      }
      sort = Sort.by(direction, property);
      pageable = PageRequest.of(req.getPage(), req.getSize(), sort);
    }

    Page<Shorts> page = shortsRepository.findShorts(req, pageable);

    Set<Long> likedShortsIds = new HashSet<>();
    if (req.getUserIdentifier() != null && !page.isEmpty()) {
      List<Long> shortsIds = page.getContent().stream().map(Shorts::getId).toList();
      likedShortsIds.addAll(shortsLikeRepository.findLikedShortsIds(req.getUserIdentifier(), shortsIds));
    }

    List<ShortsDto> dtos = page.getContent().stream().map(s -> ShortsDto.builder()
        .id(s.getId())
        .title(s.getTitle())
        .thumbnailUrl(s.getThumbnailUrl())
        .video(s.getVideo())
        .good(s.getGood())
        .readcount(s.getReadcount())
        .liked(likedShortsIds.contains(s.getId()))
        .createdAt(s.getCreatedAt())
        .latitude(s.getLatitude())
        .longitude(s.getLongitude())
        .radius(req.getRadius())
        .build())
        .toList();

    return ShortsListRes.builder()
        .content(dtos)
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

  @Transactional(readOnly = true)
  public ShortsDetailDto getShortsDetail(Long id, String userIdentifier) {
    Shorts shorts = shortsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Shorts not found: " + id));

    boolean liked = false;
    if (userIdentifier != null) {
      liked = shortsLikeRepository.existsByShortsIdAndUserIdentifier(id, userIdentifier);
    }

    return ShortsDetailDto.builder()
        .id(shorts.getId())
        .name(shorts.getName())
        .title(shorts.getTitle())
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
  public ShortsListRes getRelatedShorts(Long targetId, String userIdentifier) {
    return shortsRecommendationService.getRelatedShorts(targetId, userIdentifier);
  }
}
