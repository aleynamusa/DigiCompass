package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.GraphHopperService;
import com.digicompass.backend.controller.dto.request.RouteRequestMapDto;
import com.digicompass.backend.controller.dto.response.RouteResponseMapDto;
import com.digicompass.backend.controller.mapper.MapMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/map")
public class MapController {

        private final GraphHopperService service;
        private final MapMapper mapMapper;

    public MapController(
                GraphHopperService service,
                 MapMapper mapMapper
               ) {
        this.service = service;
            this.mapMapper = mapMapper;

    }

    @PostMapping
    public RouteResponseMapDto route (@RequestBody RouteRequestMapDto request){
        if (request.getPoints() == null || request.getPoints().size() < 2) {
            log.error("At least two points required for route calculation.");
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least two points required"
            );
        }
        try{
            log.info("Received route calculation request with {} points.", request.getPoints().size());
            return mapMapper.toRouteResponseDto(service.calculateRoute(
                    mapMapper.toModel(request.getPoints()),
                    request.getRouteType()));


        }catch (Exception e){
            log.error("Error calculating route: {}", e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error calculating route: " + e.getMessage()
            );
        }
    }


}
