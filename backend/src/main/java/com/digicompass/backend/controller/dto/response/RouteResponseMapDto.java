package com.digicompass.backend.controller.dto.response;

import com.digicompass.backend.controller.dto.PointDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteResponseMapDto {
    private List<PointDto> route;
    private double distanceKm;
    private long durationMin;

}



