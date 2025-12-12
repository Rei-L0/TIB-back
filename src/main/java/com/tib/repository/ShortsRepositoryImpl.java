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

    // 1. 메인 쿼리 (데이터 조회)
    CriteriaQuery<Shorts> cq = cb.createQuery(Shorts.class);
    Root<Shorts> shorts = cq.from(Shorts.class);

    // [핵심 해결책] 여기서 미리 Fetch Join을 걸어줍니다. (N+1 방지)
    // 리스트 조회 시 무조건 메타데이터를 함께 가져옵니다.
    if (Shorts.class.equals(cq.getResultType())) {
      shorts.fetch("shortsMetadata", JoinType.LEFT);
    }

    // Predicate 생성 (필터링)
    List<Predicate> predicates = buildPredicates(cb, cq, shorts, req);
    cq.where(predicates.toArray(new Predicate[0]));

    // 정렬 로직
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

    // 2. 카운트 쿼리 (개수 조회)
    CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
    Root<Shorts> countRoot = countQ.from(Shorts.class);
    countQ.select(cb.count(countRoot));

    // 카운트 쿼리용 Predicate 생성 (여기서는 Fetch Join이 적용되지 않음)
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

    // 메타데이터 필터 (날씨, 테마, 계절) - N+1 해결 연계 로직
    if (req.getWeather() != null || req.getTheme() != null || req.getSeason() != null) {

      Join<Shorts, ShortsMetadata> metadataJoin = null;

      // 1. 이미 Fetch Join이 되어 있는지 확인 (메인 쿼리의 경우)
      if (query instanceof CriteriaQuery && Shorts.class.equals(((CriteriaQuery<?>) query).getResultType())) {
        for (Fetch<Shorts, ?> fetch : root.getFetches()) {
          if ("shortsMetadata".equals(fetch.getAttribute().getName())) {
            // Fetch 객체를 Join으로 캐스팅 (컴파일러 우회 필요)
            metadataJoin = (Join<Shorts, ShortsMetadata>) (Object) fetch;
            break;
          }
        }
      }

      // 2. Fetch Join이 없거나 카운트 쿼리인 경우 -> 일반 Join 생성
      if (metadataJoin == null) {
        metadataJoin = root.join("shortsMetadata", JoinType.LEFT);
      }

      // 3. 조건 추가
      if (req.getWeather() != null) {
        predicates.add(cb.equal(metadataJoin.get("weather"), req.getWeather()));
      }
      if (req.getTheme() != null) {
        predicates.add(cb.like(metadataJoin.get("theme"), "%" + req.getTheme() + "%"));
      }
      if (req.getSeason() != null) {
        predicates.add(cb.equal(metadataJoin.get("season"), req.getSeason()));
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