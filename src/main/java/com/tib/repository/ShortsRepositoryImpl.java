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

    // 1. Fetch Result List
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
    String sortProp = req.getSort() != null ? req.getSort() : "id";
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
          predicates.add(cb.equal(attraction.get("gugun").get("gugunCode"), req.getGugunCode()));
        }
        if (req.getContentTypeId() != null) {
          predicates.add(cb.equal(attraction.get("contentType").get("contentTypeId"), req.getContentTypeId()));
        }
      }
    }

    if (req.getHashtag() != null && !req.getHashtag().isBlank()) {
      if (query instanceof AbstractQuery) {
        AbstractQuery<?> aq = (AbstractQuery<?>) query;
        Subquery<Long> sub = aq.subquery(Long.class);
        Root<ShortsHashtag> sh = sub.from(ShortsHashtag.class);
        Join<ShortsHashtag, Hashtag> h = sh.join("hashtag");

        sub.select(sh.get("shorts").get("id")); // select shorts ID
        sub.where(
            cb.equal(sh.get("shorts"), root),
            cb.like(h.get("name"), "%" + req.getHashtag() + "%"));
        predicates.add(cb.exists(sub));
      }
    }

    if (req.getWeather() != null || req.getTheme() != null || req.getSeason() != null) {
      if (query instanceof AbstractQuery) {
        AbstractQuery<?> aq = (AbstractQuery<?>) query;
        Subquery<Long> sub = aq.subquery(Long.class);
        Root<ShortsMetadata> m = sub.from(ShortsMetadata.class);

        sub.select(m.get("shortsId"));

        List<Predicate> metaPreds = new ArrayList<>();
        metaPreds.add(cb.equal(m.get("shorts"), root)); // correlate

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

    if (req.getStatus() != null) {
      predicates.add(cb.equal(root.get("status"), req.getStatus()));
    }

    return predicates;
  }
}
