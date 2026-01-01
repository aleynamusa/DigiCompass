package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.WeatherRecommendationService;
import com.digicompass.backend.application.models.weather.RecommendedDay;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather/recommendations")
public class WeatherRecommendationController {

    private final WeatherRecommendationService recommendationService;

    public WeatherRecommendationController(WeatherRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<RecommendedDay> getRecommendations(
            @RequestParam double lat,
            @RequestParam double lon
    ) throws JsonProcessingException {
        return recommendationService.recommendDays(lat, lon);
    }
}
