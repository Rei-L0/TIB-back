package com.tib.repository;

import com.tib.dto.NearbyAttractionDto;
import com.tib.entity.AttractionInfo;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttractionRepository extends JpaRepository<AttractionInfo, Integer> {

    @Query(value = """
            SELECT new com.tib.dto.NearbyAttractionDto(
                ai.contentId,
                ai.title,
                s.sidoName,
                g.gugunName,
                ct.contentTypeName,
                ad.overview,
                ai.firstImage,
                ai.latitude,
                ai.longitude,
                (6371 * acos(cos(radians(:latitude)) * cos(radians(ai.latitude))
                * cos(radians(ai.longitude) - radians(:longitude))
                + sin(radians(:latitude)) * sin(radians(ai.latitude)))) * 1000,
                0L
            )
            FROM AttractionInfo ai
            JOIN ai.sido s
            JOIN ai.gugun g
            JOIN ai.contentType ct
            LEFT JOIN ai.attractionDescription ad
            WHERE
                (:contentTypeId IS NULL OR ai.contentType.contentTypeId = :contentTypeId)
                AND (6371 * acos(cos(radians(:latitude)) * cos(radians(ai.latitude))
                * cos(radians(ai.longitude) - radians(:longitude))
                + sin(radians(:latitude)) * sin(radians(ai.latitude)))) * 1000 <= :radius
            ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(ai.latitude))
                * cos(radians(ai.longitude) - radians(:longitude))
                + sin(radians(:latitude)) * sin(radians(ai.latitude)))) * 1000 ASC
            LIMIT :limit
            """)
    List<NearbyAttractionDto> findNearbyAttractions(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radius") double radius,
            @Param("contentTypeId") Integer contentTypeId,
            @Param("limit") int limit);

    Page<AttractionInfo> findByTitleContainingOrAddr1Containing(String title, String addr1, Pageable pageable);
}
