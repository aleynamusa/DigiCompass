package com.digicompass.backend.application.services.helpers;

import com.digicompass.backend.application.interfaces.WeatherScoringService;
import com.digicompass.backend.application.models.weather.RecommendedDay;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherScoringImpl implements WeatherScoringService {

    public RecommendedDay scoreDay(
            String date,
            int weatherCode,
            double maxTemp,
            double minTemp,
            int precipitation) {
        int score = 100;
        List<String> reasons = new ArrayList<>();

        if (precipitation > 60) {
            score -= 50;
            reasons.add("Heavy rain");
        } else if (precipitation > 30) {
            score -= 25;
            reasons.add("Possible rain");
        }

        if (maxTemp < 5 || maxTemp > 35) {
            score -= 40;
            reasons.add("Extreme temperature");
        } else if (maxTemp < 12 || maxTemp > 28) {
            score -= 15;
            reasons.add("Suboptimal temperature");
        }

        if (weatherCode >= 95) {
            score -= 60;
            reasons.add("Thunderstorm");
        }

        score = Math.max(score, 0);

        return new RecommendedDay(
                date,
                score,
                labelFromScore(score),
                reasons,
                maxTemp,
                minTemp,
                precipitation,
                weatherCode
        );
    }

    private String labelFromScore(int score) {
        if (score >= 80) return "Perfect";
        if (score >= 60) return "Good";
        if (score >= 40) return "Caution";
        return "Avoid";
    }
}

