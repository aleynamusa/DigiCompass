package com.digicompass.backend.application.models.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendedDay {
    private String date;
    private int score;
    private String label;
    private List<String> reasons;

    private double maxTemp;
    private double minTemp;
    private int precipitation;
    private int weatherCode;
}
