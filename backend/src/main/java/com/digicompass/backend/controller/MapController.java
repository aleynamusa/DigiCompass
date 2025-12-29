package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.GraphHopperService;
import com.digicompass.backend.controller.dto.request.RouteRequestMapDto;
import com.digicompass.backend.controller.dto.response.RouteResponseMapDto;
import com.digicompass.backend.controller.mapper.MapMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
        if (request.getPoints() == null || request.getPoints().size() < 2) {
            log.error("[CONTROLLER] At least two points required for route calculation.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try{
            log.info("[CONTROLLER] Received route calculation request with {} points.", request.getPoints().size());
            RouteResponseMapDto routeInfo =  mapMapper.toRouteResponseDto(service.calculateRoute(
                    mapMapper.toModel(request.getPoints()),
                    request.getRouteType()));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(routeInfo);


        }catch (Exception e){
            log.error("[CONTROLLER] Error calculating route: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/coords")
    public ResponseEntity<String> getCurrentLocation(@RequestParam(required = true) double latitude,
                                                     @RequestParam(required = true) double longitude){
            try{
                return ResponseEntity.status(HttpStatus.OK).body(service.getCurrentLocationAsCity(latitude, longitude));
            }
            catch (Exception e){
                log.error("[CONTROLLER] Error getting current location: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

    }


}
