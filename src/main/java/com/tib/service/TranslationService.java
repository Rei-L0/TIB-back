package com.tib.service;

import com.tib.entity.Shorts;
import com.tib.entity.ShortsI18n;
import com.tib.repository.ShortsI18nRepository;
import com.tib.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final DeepLTranslateService deepLTranslateService;
    private final ShortsI18nRepository i18nRepository;
    private final ShortsRepository shortsRepository;

    private static final String[] TARGET_LANGS = {"EN", "ZH", "JA"};

    @Async
    @Transactional
    public void translateShortsAsync(Long shortsId) {
        Shorts shorts = shortsRepository.findById(shortsId).orElse(null);
        if (shorts == null) {
            log.warn("번역 대상 Shorts 없음: id={}", shortsId);
            return;
        }

        for (String lang : TARGET_LANGS) {
            try {
                String translatedTitle = deepLTranslateService.translate(shorts.getTitle(), lang);

                if (translatedTitle != null) {
                    ShortsI18n i18n = ShortsI18n.builder()
                            .shortsId(shortsId)
                            .lang(lang.toLowerCase())  // DB에는 소문자로 저장 (en, zh, ja)
                            .title(translatedTitle)
                            .build();

                    i18nRepository.save(i18n);
                    log.info("번역 완료: shortsId={}, lang={}", shortsId, lang);
                }
            } catch (Exception e) {
                log.error("번역 실패: shortsId={}, lang={}, error={}", shortsId, lang, e.getMessage());
            }
        }
    }
}