package com.tib.service;

import com.tib.dto.NearbyAttractionDto;
import com.tib.dto.NearbyAttractionProjection;
import com.tib.dto.NearbyAttractionReq;
import com.tib.dto.NearbyAttractionRes;
import com.tib.repository.AttractionRepository;
import com.tib.repository.ShortsRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttractionService {

  private final AttractionRepository attractionRepository;
  private final ShortsRepository shortsRepository;

  @Transactional(readOnly = true)
  public NearbyAttractionRes getNearbyAttractions(NearbyAttractionReq req) {
    // 1. Find nearby attractions
    List<NearbyAttractionProjection> attractions = attractionRepository.findNearbyAttractions(
        req.getLatitude(),
        req.getLongitude(),
        req.getRadius(),
        req.getContentTypeId(),
        req.getLimit());

    // 2. Extract IDs for batch count
    List<Integer> contentIds = attractions.stream()
        .map(NearbyAttractionProjection::getContentId)
        .toList();

    // 3. Batch count shorts
    Map<Integer, Long> shortsCounts = Map.of();
    if (!contentIds.isEmpty()) {
      shortsCounts = shortsRepository.countByAttractionContentIds(contentIds)
          .stream()
          .collect(Collectors.toMap(
              row -> (Integer) row[0],
              row -> (Long) row[1]));
    }

    // 4. Assemble DTOs
    Map<Integer, Long> finalShortsCounts = shortsCounts;
    List<NearbyAttractionDto> dtos = attractions.stream()
        .map(p -> NearbyAttractionDto.builder()
            .contentId(p.getContentId())
            .title(p.getTitle())
            .sidoName(p.getSidoName())
            .gugunName(p.getGugunName())
            .contentTypeName(p.getContentTypeName())
            .overview(p.getOverview())
            .firstImage(p.getFirstImage())
            .distance(p.getDistance())
            .shortsCount(finalShortsCounts.getOrDefault(p.getContentId(), 0L))
            .build())
        .toList();

    return NearbyAttractionRes.builder()
        .center(NearbyAttractionRes.Center.builder()
            .latitude(req.getLatitude())
            .longitude(req.getLongitude())
            .build())
        .radius(req.getRadius())
        .attractions(dtos)
        .build();
  }
}
