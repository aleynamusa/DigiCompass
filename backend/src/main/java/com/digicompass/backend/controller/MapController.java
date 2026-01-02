package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.GraphHopperService;
import com.digicompass.backend.controller.dto.request.RouteRequestMapDto;
import com.digicompass.backend.controller.dto.response.RouteResponseMapDto;
import com.digicompass.backend.controller.mapper.MapMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@Slf4j
@RequestMapping("/map")
public class MapController {

        private final GraphHopperService service;
        private final MapMapper mapMapper;

        @Autowired
    public MapController(
                GraphHopperService service,
                 MapMapper mapMapper
               ) {
        this.service = service;
            this.mapMapper = mapMapper;

    }

    @PostMapping
    public ResponseEntity<RouteResponseMapDto> routeInfo(@Valid @RequestBody RouteRequestMapDto request){

            log.info("[CONTROLLER] Received route calculation request with {} points.", request.getPoints().size());
            RouteResponseMapDto routeInfo =  mapMapper.toRouteResponseDto(service.calculateRoute(
                    mapMapper.toModel(request.getPoints()),
                    request.getRouteType()));

            log.info("[CONTROLLER] Calculated distance {} and duration {}", routeInfo.getDistanceKm(), routeInfo.getDurationHour());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(routeInfo);

    }

    @GetMapping("/coords")
    public ResponseEntity<String> getCurrentLocation(@RequestParam double latitude,
                                                     @RequestParam double longitude){
            log.info("[CONTROLLER] Received request to get current location");
            return ResponseEntity.status(HttpStatus.OK).body(service.getCurrentLocationAsCity(latitude, longitude));


    }


}
