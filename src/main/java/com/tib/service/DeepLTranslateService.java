package com.tib.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DeepLTranslateService {

    @Value("${deepl.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String translate(String text, String targetLang) {
        if (text == null || text.isBlank()) return text;

        try {
            String url = "https://api-free.deepl.com/v2/translate";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // URL 인코딩 추가
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            String body = String.format("auth_key=%s&text=%s&source_lang=KO&target_lang=%s",
                    apiKey, encodedText, targetLang.toUpperCase());

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            List<Map> translations = (List<Map>) response.getBody().get("translations");
            String result = (String) translations.get(0).get("text");

            log.info("DeepL 번역: {} -> {} ({})", text, result, targetLang);
            return result;
        } catch (Exception e) {
            log.error("DeepL 번역 실패: {}", e.getMessage());
            return null;
        }
    }
}