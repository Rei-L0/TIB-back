package com.tib.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Object getWeather(double lat, double lng, String date) {
        try {
            date = date.replace("\"", "");

            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList" +
                            "?serviceKey=%s&numOfRows=10&pageNo=1&dataType=JSON" +
                            "&dataCd=ASOS&dateCd=DAY&startDt=%s&endDt=%s&stnIds=159",
                    encodedKey, date, date
            );

            log.info("요청 URL: {}", url);

            // URI 객체로 직접 호출 (이중 인코딩 방지)
            URI uri = new URI(url);
            return restTemplate.getForObject(uri, Map.class);
        } catch (Exception e) {
            log.error("날씨 API 호출 실패: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }
}