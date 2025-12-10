package com.tib.repository;

import com.tib.dto.NearbyAttractionProjection;
import com.tib.entity.AttractionInfo;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttractionRepository extends JpaRepository<AttractionInfo, Integer> {

  @Query(value = """
      SELECT
          ai.content_id AS contentId,
          ai.title AS title,
          ai.first_image AS firstImage,
          s.sido_name AS sidoName,
          g.gugun_name AS gugunName,
          ct.content_type_name AS contentTypeName,
          ad.overview AS overview,
          (6371 * acos(cos(radians(:latitude)) * cos(radians(ai.latitude))
          * cos(radians(ai.longitude) - radians(:longitude))
          + sin(radians(:latitude)) * sin(radians(ai.latitude)))) * 1000 AS distance
      FROM attraction_info ai
      JOIN sido s ON ai.sido_code = s.sido_code
      JOIN gugun g ON ai.gugun_code = g.gugun_code AND ai.sido_code = g.sido_code
      JOIN content_type ct ON ai.content_type_id = ct.content_type_id
      LEFT JOIN attraction_description ad ON ai.content_id = ad.content_id
      WHERE
          (:contentTypeId IS NULL OR ai.content_type_id = :contentTypeId)
          AND (6371 * acos(cos(radians(:latitude)) * cos(radians(ai.latitude))
          * cos(radians(ai.longitude) - radians(:longitude))
          + sin(radians(:latitude)) * sin(radians(ai.latitude)))) * 1000 <= :radius
      ORDER BY distance ASC
      LIMIT :limit
      """, nativeQuery = true)
  List<NearbyAttractionProjection> findNearbyAttractions(
      @Param("latitude") BigDecimal latitude,
      @Param("longitude") BigDecimal longitude,
      @Param("radius") double radius,
      @Param("contentTypeId") Integer contentTypeId,
      @Param("limit") int limit);
}
