package com.tib.controller;

import com.tib.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public Object getWeather(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam("date") String date
    ) {
        return weatherService.getWeather(lat, lng, date);
    }
}