package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.weather.RecommendedDay;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.util.List;


public interface WeatherRecommendationService {
    List<RecommendedDay> recommendDays(
            double latitude,
            double longitude) throws JsonProcessingException;
}
