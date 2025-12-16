package com.tib.repository;

import com.tib.entity.ShortsI18n;
import com.tib.entity.ShortsI18nId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortsI18nRepository extends JpaRepository<ShortsI18n, ShortsI18nId> {
    Optional<ShortsI18n> findByShortsIdAndLang(Long shortsId, String lang);
}