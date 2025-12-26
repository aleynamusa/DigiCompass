package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.PointDto;
import lombok.Data;

import java.util.List;

@Data
public class RouteRequestMapDto {
    private List<PointDto> points;
    private String routeType;

}
