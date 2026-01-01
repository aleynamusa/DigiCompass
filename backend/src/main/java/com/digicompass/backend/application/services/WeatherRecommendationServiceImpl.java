package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.WeatherRecommendationService;
import com.digicompass.backend.application.interfaces.WeatherScoringService;
import com.digicompass.backend.application.interfaces.WeatherService;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.RecommendedDay;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherRecommendationServiceImpl implements WeatherRecommendationService {

    private final WeatherService weatherService;
    private final WeatherScoringService scoringService;

    @Autowired
    public WeatherRecommendationServiceImpl(WeatherService weatherService, WeatherScoringService scoringService) {
        this.weatherService = weatherService;
        this.scoringService = scoringService;
    }

    @Override
    public List<RecommendedDay> recommendDays(
            double latitude,
            double longitude
    ) throws JsonProcessingException {

        DailyWeather daily = weatherService.fetchWeatherDaily(latitude, longitude);

        List<RecommendedDay> results = new ArrayList<>();

        for (int i = 0; i < daily.getTime().size(); i++) {
            results.add(
                    scoringService.scoreDay(
                            daily.getTime().get(i),
                            daily.getWeatherCode().get(i),
                            daily.getMaxTemperature().get(i),
                            daily.getMinTemperature().get(i),
                            daily.getPrecipitation().get(i))
            );
        }

        return results;
    }
}

