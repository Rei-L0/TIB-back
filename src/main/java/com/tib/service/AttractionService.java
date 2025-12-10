package com.tib.service;

import com.tib.dto.AttractionDetailRes;
import com.tib.dto.AttractionDetailRes.Description;
import com.tib.dto.AttractionDetailRes.Detail;
import com.tib.dto.NearbyAttractionDto;
import com.tib.dto.NearbyAttractionProjection;
import com.tib.dto.NearbyAttractionReq;
import com.tib.dto.NearbyAttractionRes;
import com.tib.entity.AttractionDescription;
import com.tib.entity.AttractionDetail;
import com.tib.entity.AttractionInfo;
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
        List<NearbyAttractionProjection> attractions = attractionRepository.findNearbyAttractions(
                req.getLatitude(),
                req.getLongitude(),
                req.getRadius(),
                req.getContentTypeId(),
                req.getLimit());
        List<Integer> contentIds = attractions.stream()
                .map(NearbyAttractionProjection::getContentId)
                .toList();

        Map<Integer, Long> shortsCounts = Map.of();
        if (!contentIds.isEmpty()) {
            shortsCounts = shortsRepository.countByAttractionContentIds(contentIds)
                    .stream()
                    .collect(Collectors.toMap(
                            row -> (Integer) row[0],
                            row -> (Long) row[1]));
        }

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

    @Transactional(readOnly = true)
    public AttractionDetailRes getAttractionDetail(Integer contentId) {
        AttractionInfo attraction = attractionRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Attraction not found: " + contentId));

        Long shortsCount = shortsRepository.countByAttractionContentIds(List.of(contentId))
                .stream()
                .findFirst()
                .map(row -> (Long) row[1])
                .orElse(0L);

        Detail detailDto = null;
        if (attraction.getAttractionDetail() != null) {
            AttractionDetail detail = attraction.getAttractionDetail();
            detailDto = Detail.builder()
                    .cat1(detail.getCat1())
                    .cat2(detail.getCat2())
                    .cat3(detail.getCat3())
                    .createdTime(detail.getCreatedTime())
                    .modifiedTime(detail.getModifiedTime())
                    .booktour(detail.getBooktour())
                    .build();
        }

        Description descriptionDto = null;
        if (attraction.getAttractionDescription() != null) {
            AttractionDescription desc = attraction.getAttractionDescription();
            descriptionDto = Description.builder()
                    .homepage(desc.getHomepage())
                    .overview(desc.getOverview())
                    .telname(desc.getTelname())
                    .build();
        }

        return AttractionDetailRes.builder()
                .contentId(attraction.getContentId())
                .title(attraction.getTitle())
                .addr1(attraction.getAddr1())
                .addr2(attraction.getAddr2())
                .zipcode(attraction.getZipcode())
                .tel(attraction.getTel())
                .firstImage(attraction.getFirstImage())
                .sidoName(attraction.getSido().getSidoName())
                .gugunName(attraction.getGugun().getGugunName())
                .shortsCount(shortsCount)
                .detail(detailDto)
                .description(descriptionDto)
                .build();
    }
}
