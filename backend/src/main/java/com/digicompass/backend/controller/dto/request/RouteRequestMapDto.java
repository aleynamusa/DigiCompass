package com.digicompass.backend.controller.dto.request;

import com.digicompass.backend.controller.dto.PointDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteRequestMapDto {
    @NotEmpty(message = "Please select points on the map")
    @Size(max = 5, min = 2, message = "You cannot select more than 5 points.")
    private List<PointDto> points;

    private String routeType;

}
