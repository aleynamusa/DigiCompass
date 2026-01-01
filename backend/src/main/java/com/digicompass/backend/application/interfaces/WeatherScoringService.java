package com.digicompass.backend.application.interfaces;

import com.digicompass.backend.application.models.weather.RecommendedDay;

public interface WeatherScoringService {
    RecommendedDay scoreDay(
            String date,
            int weatherCode,
            double maxTemp,
            double minTemp,
            int precipitation);

}
