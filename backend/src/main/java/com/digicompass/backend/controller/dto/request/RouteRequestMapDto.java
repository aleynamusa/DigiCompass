package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.PointDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteRequestMapDto {
    private List<PointDto> points;
    private String routeType;

}
