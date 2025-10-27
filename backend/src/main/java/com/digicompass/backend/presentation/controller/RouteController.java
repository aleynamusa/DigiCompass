package com.digicompass.backend.presentation.controller;

import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.infrastucture.persistence.models.Route;
import com.digicompass.backend.presentation.controller.dto.RouteDto;
import com.digicompass.backend.presentation.controller.dto.RouteGeometryDto;
import com.digicompass.backend.presentation.controller.mapper.RouteMapperController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;
    private final RouteMapperController routeMapper;
    private static final Logger LOGGER = Logger.getLogger( RouteController.class.getName() );


    public RouteController(RouteService routeService, RouteMapperController userMapper) {
        this.routeService = routeService;
        this.routeMapper = userMapper;
    }


    @GetMapping()
    public ResponseEntity<List<Route>> getRoutes() {
        try {
            List<Route> routes = routeService.getRoutes();
            LOGGER.info("Fetched routes: " + routes.size());
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
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(routeService.getRoutesByKeyword(keyword));
            return ResponseEntity.ok(routes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/distance")
    public ResponseEntity<List<RouteDto>> getRoutesByDistance(@RequestParam float distance) {
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(routeService.getRoutesByDistance(distance));
            return ResponseEntity.ok(routes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type")
    public ResponseEntity<List<RouteDto>> getRoutesByType(@RequestParam String type) {
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(routeService.getRoutesByType(type));
            return ResponseEntity.ok(routes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/difficulty")
    public ResponseEntity<List<RouteDto>> getRoutesByDifficulty(@RequestParam String difficulty) {
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(routeService.getRouteByDifficulty(difficulty));
            return ResponseEntity.ok(routes);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<RouteDto>> getAllRoutes(@RequestParam(required = false) String type,
                                                       @RequestParam(required = false) String difficulty,
                                                       @RequestParam(required = false) Float distance) {
        try {
            List<RouteDto> routes = routeMapper.toControllerRoute(
                    routeService.getFilteredRoutes(type, difficulty, distance)
            );
            LOGGER.info("Filtered routes: " + routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
