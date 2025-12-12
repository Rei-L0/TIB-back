package com.tib.repository;

import com.tib.dto.ShortsListReq;
import com.tib.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShortsRepositoryImpl implements ShortsRepositoryCustom {

  @PersistenceContext
  private final EntityManager em;

  @Override
  public Page<Shorts> findShorts(ShortsListReq req, Pageable pageable) {
    CriteriaBuilder cb = em.getCriteriaBuilder();

    CriteriaQuery<Shorts> cq = cb.createQuery(Shorts.class);
    Root<Shorts> shorts = cq.from(Shorts.class);

    List<Predicate> predicates = buildPredicates(cb, cq, shorts, req);
    cq.where(predicates.toArray(new Predicate[0]));

    if (pageable.getSort().isSorted()) {
      List<Order> orders = new ArrayList<>();
      pageable.getSort().forEach(order -> {
        Path<?> path = shorts.get("id");
        if ("id".equals(order.getProperty()))
          path = shorts.get("id");
        else if ("readcount".equals(order.getProperty()))
          path = shorts.get("readcount");
        else if ("good".equals(order.getProperty()))
          path = shorts.get("good");
        else if ("createdAt".equals(order.getProperty()))
          path = shorts.get("createdAt");

        if (order.isAscending())
          orders.add(cb.asc(path));
        else
          orders.add(cb.desc(path));
      });
      cq.orderBy(orders);
    } else {
      applyManualSort(cb, cq, shorts, req);
    }

    TypedQuery<Shorts> query = em.createQuery(cq);
    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());
    List<Shorts> content = query.getResultList();

    CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
    Root<Shorts> countRoot = countQ.from(Shorts.class);
    countQ.select(cb.count(countRoot));
    List<Predicate> countPredicates = buildPredicates(cb, countQ, countRoot, req);
    countQ.where(countPredicates.toArray(new Predicate[0]));

    long total = em.createQuery(countQ).getSingleResult();

    return new PageImpl<>(content, pageable, total);
  }

  private void applyManualSort(CriteriaBuilder cb, CriteriaQuery<Shorts> cq, Root<Shorts> shorts, ShortsListReq req) {
    Path<?> sortPath;
    String sortProp = req.getSort() != null ? req.getSort() : "createdAt";
    String order = req.getOrder() != null ? req.getOrder() : "desc";

    switch (sortProp) {
      case "readcount":
      case "views":
        sortPath = shorts.get("readcount");
        break;
      case "good":
      case "popular":
        sortPath = shorts.get("good");
        break;
      case "createdAt":
      case "created_at":
      case "date":
        sortPath = shorts.get("createdAt");
        break;
      case "id":
      default:
        sortPath = shorts.get("id");
    }

    if ("asc".equalsIgnoreCase(order)) {
      cq.orderBy(cb.asc(sortPath));
    } else {
      cq.orderBy(cb.desc(sortPath));
    }
  }

  private List<Predicate> buildPredicates(CriteriaBuilder cb, CommonAbstractCriteria query, Root<Shorts> root,
      ShortsListReq req) {
    List<Predicate> predicates = new ArrayList<>();

    // 관광지 ID 필터
    if (req.getContentId() != null) {
      Join<Shorts, AttractionInfo> attraction = root.join("attractionInfo", JoinType.LEFT);
      predicates.add(cb.equal(attraction.get("contentId"), req.getContentId()));
    } else {
      boolean needAttraction = req.getSidoCode() != null || req.getGugunCode() != null
          || req.getContentTypeId() != null;
      if (needAttraction) {
        Join<Shorts, AttractionInfo> attraction = root.join("attractionInfo", JoinType.LEFT);
        if (req.getSidoCode() != null) {
          predicates.add(cb.equal(attraction.get("sido").get("sidoCode"), req.getSidoCode()));
        }
        if (req.getGugunCode() != null) {
          predicates.add(cb.equal(attraction.get("gugun").get("id").get("gugunCode"), req.getGugunCode()));
        }
        if (req.getContentTypeId() != null) {
          predicates.add(cb.equal(attraction.get("contentType").get("contentTypeId"), req.getContentTypeId()));
        }
      }
    }

    // 해시태그 필터
    if (req.getHashtag() != null && !req.getHashtag().isBlank()) {
      if (query instanceof AbstractQuery) {
        AbstractQuery<?> aq = (AbstractQuery<?>) query;
        Subquery<Long> sub = aq.subquery(Long.class);
        Root<ShortsHashtag> sh = sub.from(ShortsHashtag.class);
        Join<ShortsHashtag, Hashtag> h = sh.join("hashtag");

        sub.select(sh.get("shorts").get("id"));
        sub.where(
            cb.equal(sh.get("shorts"), root),
            cb.like(h.get("name"), "%" + req.getHashtag() + "%"));
        predicates.add(cb.exists(sub));
      }
    }

    // 메타데이터 필터 (날씨, 테마, 계절)
    if (req.getWeather() != null || req.getTheme() != null || req.getSeason() != null) {
      if (query instanceof AbstractQuery) {
        AbstractQuery<?> aq = (AbstractQuery<?>) query;
        Subquery<Long> sub = aq.subquery(Long.class);
        Root<ShortsMetadata> m = sub.from(ShortsMetadata.class);

        sub.select(m.get("shortsId"));

        List<Predicate> metaPreds = new ArrayList<>();
        metaPreds.add(cb.equal(m.get("shorts"), root));

        if (req.getWeather() != null)
          metaPreds.add(cb.equal(m.get("weather"), req.getWeather()));
        if (req.getTheme() != null)
          metaPreds.add(cb.like(m.get("theme"), "%" + req.getTheme() + "%"));
        if (req.getSeason() != null)
          metaPreds.add(cb.equal(m.get("season"), req.getSeason()));

        sub.where(metaPreds.toArray(new Predicate[0]));
        predicates.add(cb.exists(sub));
      }
    }

    // 상태 필터
    if (req.getStatus() != null) {
      predicates.add(cb.equal(root.get("status"), req.getStatus()));
    }

    // 위치 기반 필터 (바운딩 박스 방식)
    if (req.getLatitude() != null && req.getLongitude() != null && req.getRadius() != null) {
      double latDiff = req.getRadius() / 111000.0;
      double lonDiff = req.getRadius() / (111000.0 * Math.cos(Math.toRadians(req.getLatitude())));

      predicates.add(cb.greaterThanOrEqualTo(root.get("latitude"), req.getLatitude() - latDiff));
      predicates.add(cb.lessThanOrEqualTo(root.get("latitude"), req.getLatitude() + latDiff));
      predicates.add(cb.greaterThanOrEqualTo(root.get("longitude"), req.getLongitude() - lonDiff));
      predicates.add(cb.lessThanOrEqualTo(root.get("longitude"), req.getLongitude() + lonDiff));
    }

    return predicates;
  }

  @Override
  public List<Shorts> findCandidates(Long targetId, Integer gugunCode, String theme, int limit) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Shorts> cq = cb.createQuery(Shorts.class);
    Root<Shorts> root = cq.from(Shorts.class);

    List<Predicate> predicates = new ArrayList<>();

    // 1. 자기 자신 제외
    predicates.add(cb.notEqual(root.get("id"), targetId));

    // 2. OR 조건: 같은 지역 OR 같은 테마
    List<Predicate> orPredicates = new ArrayList<>();

    if (gugunCode != null) {
      Join<Shorts, AttractionInfo> attraction = root.join("attractionInfo", JoinType.LEFT);
      orPredicates.add(cb.equal(attraction.get("gugunCode"), gugunCode));
    }

    if (theme != null && !theme.isBlank()) {
      Join<Shorts, ShortsMetadata> metadata = root.join("shortsMetadata", JoinType.LEFT);
      orPredicates.add(cb.equal(metadata.get("theme"), theme));
    }

    if (!orPredicates.isEmpty()) {
      predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
    }

    cq.where(predicates.toArray(new Predicate[0]));
    cq.orderBy(cb.desc(root.get("createdAt")));

    return em.createQuery(cq)
        .setMaxResults(limit)
        .getResultList();
  }
}