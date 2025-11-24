package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import com.digicompass.backend.controller.mapper.RouteMapperController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/route")
@Slf4j
public class RouteController {

    private final RouteService routeService;
    private final RouteMapperController routeMapper;


    public RouteController(RouteService routeService, RouteMapperController userMapper) {
        this.routeService = routeService;
        this.routeMapper = userMapper;
    }


    @GetMapping()
    public ResponseEntity<List<Route>> getRoutes() {
        try {
            List<Route> routes = routeService.getRoutes();
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
}
