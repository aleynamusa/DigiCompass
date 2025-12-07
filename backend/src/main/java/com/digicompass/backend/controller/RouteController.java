package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.controller.dto.GeoJsonDto;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import com.digicompass.backend.controller.dto.request.RouteRequestDto;
import com.digicompass.backend.controller.mapper.RouteMapperController;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/route")
@Slf4j
public class     RouteController {

    private final RouteService routeService;
    private final RouteMapperController routeMapper;


    public RouteController(RouteService routeService, RouteMapperController userMapper) {
        this.routeService = routeService;
        this.routeMapper = userMapper;
    }


    @GetMapping()
    public ResponseEntity<List<RouteDto>> getRoutes() {
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(routeService.getRoutes());
            log.info("[CONTROLLER] Fetched routes: {}", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/geometry")
    public ResponseEntity<RouteGeometryDto> getRouteGeometry(@PathVariable Long id) {
        RouteGeometryDto route = routeMapper.toControllerGeometry(routeService.getRouteById(id));

        try {
            return ResponseEntity.ok(
                    route
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/keyword")
    public ResponseEntity<List<RouteDto>> getRoutesByKeyword(@RequestParam String keyword) {

        try{
            List<RouteDto> routes = routeMapper.toControllerRoute(
                    routeService.searchRoutes(keyword)
            );
            log.info("Searched routes: {}", routes.size());
            return ResponseEntity.ok(routes);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Route>> getAllRoutes(@RequestParam(required = false) String type,
                                                       @RequestParam(required = false) String difficulty,
                                                       @RequestParam(required = false) Float distance) {
        try {
            List<Route> routes =
                    routeService.getFilteredRoutes(type, difficulty, distance);
            log.info("Filtered routes: {}", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/liked/{userId}")
    public ResponseEntity<List<RouteDto>> getLikedRoutesByUserId(@PathVariable Long userId) {
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(
                    routeService.getLikedRoutesByUserId(userId)
            );
            log.info("[CONTROLLER] Fetched liked routes for user with id {}: {}", userId, routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<RouteRequestDto> createRoute(@RequestBody RouteRequestDto routeDto) {
        try {
            routeService.saveRoute(routeMapper.toDomain(routeDto));
            log.info("[CONTROLLER] Created route.");
            return ResponseEntity.status(HttpStatus.CREATED).body(routeDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/calculate-distance")
    public ResponseEntity<Double> calculateDistance(@RequestBody GeoJsonDto geoJson) {
        try {
            log.info("[CONTROLLER] Calculating distance.");
            double distance = routeService.calculateDistanceFromGeoJson(routeMapper.toDomainJson(geoJson));
            log.debug("[CONTROLLER] Calculated distance: {}", distance);
            return ResponseEntity.ok(distance);
        } catch (IllegalArgumentException e) {
            log.error("[CONTROLLER] " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            log.error("[CONTROLLER] Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
