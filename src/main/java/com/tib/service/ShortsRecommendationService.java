package com.tib.service;

import com.tib.dto.ShortsDto;
import com.tib.dto.ShortsListRes;
import com.tib.entity.Shorts;
import com.tib.entity.ShortsHashtag;
import com.tib.entity.ShortsI18n;
import com.tib.repository.ShortsHashtagRepository;
import com.tib.repository.ShortsLikeRepository;
import com.tib.repository.ShortsRepository;
import com.tib.repository.ShortsI18nRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortsRecommendationService {

  private final ShortsRepository shortsRepository;
  private final ShortsHashtagRepository shortsHashtagRepository;
  private final ShortsLikeRepository shortsLikeRepository;
  private final ShortsI18nRepository shortsI18nRepository;

  @Transactional(readOnly = true)
  public ShortsListRes getRelatedShorts(Long targetId, String userIdentifier, String lang) {
    Shorts target = shortsRepository.findById(targetId)
            .orElseThrow(() -> new IllegalArgumentException("Shorts not found with id: " + targetId));

    Integer gugunCode = target.getAttractionInfo() != null ? target.getAttractionInfo().getGugunCode() : null;
    String theme = target.getShortsMetadata() != null ? target.getShortsMetadata().getTheme() : null;

    List<Shorts> candidates = shortsRepository.findCandidates(targetId, gugunCode, theme, 100);

    List<Shorts> allShorts = new ArrayList<>(candidates);
    allShorts.add(target);
    List<ShortsHashtag> allHashtags = shortsHashtagRepository.findByShortsIn(allShorts);

    Map<Long, List<String>> hashtagsMap = allHashtags.stream()
            .collect(Collectors.groupingBy(
                    sh -> sh.getShorts().getId(),
                    Collectors.mapping(sh -> sh.getHashtag().getName(), Collectors.toList())));

    List<String> targetTags = hashtagsMap.getOrDefault(targetId, Collections.emptyList());

    List<Shorts> top10 = candidates.stream()
            .sorted((s1, s2) -> {
              double score1 = calculateSimilarityScore(target, s1, targetTags,
                      hashtagsMap.getOrDefault(s1.getId(), Collections.emptyList()));
              double score2 = calculateSimilarityScore(target, s2, targetTags,
                      hashtagsMap.getOrDefault(s2.getId(), Collections.emptyList()));
              return Double.compare(score2, score1);
            })
            .limit(10)
            .toList();

    Set<Long> likedShortsIds = new HashSet<>();
    if (userIdentifier != null && !top10.isEmpty()) {
      List<Long> shortsIds = top10.stream().map(Shorts::getId).toList();
      likedShortsIds.addAll(shortsLikeRepository.findLikedShortsIds(userIdentifier, shortsIds));
    }

    List<ShortsDto> dtos = top10.stream()
            .map(s -> ShortsDto.builder()
                    .id(s.getId())
                    .title(getTranslatedTitle(s, lang))
                    .thumbnailUrl(s.getThumbnailUrl())
                    .video(s.getVideo())
                    .good(s.getGood())
                    .readcount(s.getReadcount())
                    .liked(likedShortsIds.contains(s.getId()))
                    .createdAt(s.getCreatedAt())
                    .latitude(s.getLatitude())
                    .longitude(s.getLongitude())
                    .build())
            .toList();

    return ShortsListRes.builder()
            .content(dtos)
            .page(0)
            .size(dtos.size())
            .totalElements(dtos.size())
            .totalPages(1)
            .build();
  }

  private String getTranslatedTitle(Shorts shorts, String lang) {
    if (lang == null || lang.equals("ko")) {
      return shorts.getTitle();
    }

    return shortsI18nRepository.findByShortsIdAndLang(shorts.getId(), lang)
            .map(ShortsI18n::getTitle)
            .orElse(shorts.getTitle());
  }

  private double calculateSimilarityScore(Shorts target, Shorts candidate, List<String> targetTags,
                                          List<String> candidateTags) {
    double score = 0;

    if (target.getShortsMetadata() != null && candidate.getShortsMetadata() != null) {
      String targetTheme = target.getShortsMetadata().getTheme();
      String candidateTheme = candidate.getShortsMetadata().getTheme();
      if (targetTheme != null && targetTheme.equals(candidateTheme)) {
        score += 50;
      }

      String targetWeather = target.getShortsMetadata().getWeather();
      String candidateWeather = candidate.getShortsMetadata().getWeather();
      if (targetWeather != null && targetWeather.equals(candidateWeather)) {
        score += 20;
      }

      String targetSeason = target.getShortsMetadata().getSeason();
      String candidateSeason = candidate.getShortsMetadata().getSeason();
      if (targetSeason != null && targetSeason.equals(candidateSeason)) {
        score += 20;
      }
    }

    if (target.getLatitude() != null && target.getLongitude() != null
            && candidate.getLatitude() != null && candidate.getLongitude() != null) {
      double dist = getDistance(
              target.getLatitude().doubleValue(),
              target.getLongitude().doubleValue(),
              candidate.getLatitude().doubleValue(),
              candidate.getLongitude().doubleValue()
      );
      if (dist <= 5.0) {
        score += 30;
      } else if (isSameGugun(target, candidate)) {
        score += 10;
      }
    } else if (isSameGugun(target, candidate)) {
      score += 10;
    }

    long matchingTags = countMatchingTags(targetTags, candidateTags);
    score += (matchingTags * 10);

    if (candidate.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1))) {
      score += 5;
    }

    return score;
  }

  private boolean isSameGugun(Shorts t, Shorts c) {
    if (t.getAttractionInfo() == null || c.getAttractionInfo() == null)
      return false;
    return Objects.equals(t.getAttractionInfo().getGugunCode(), c.getAttractionInfo().getGugunCode());
  }

  private double getDistance(double lat1, double lon1, double lat2, double lon2) {
    double R = 6371;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private long countMatchingTags(List<String> tags1, List<String> tags2) {
    List<String> intersection = new ArrayList<>(tags1);
    intersection.retainAll(tags2);
    return intersection.size();
  }
}