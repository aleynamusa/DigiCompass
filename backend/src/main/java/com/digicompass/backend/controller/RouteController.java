package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.RouteService;
import com.digicompass.backend.application.models.Route;
import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.controller.dto.GeoJsonDto;
import com.digicompass.backend.controller.dto.RouteDto;
import com.digicompass.backend.controller.dto.RouteGeometryDto;
import com.digicompass.backend.controller.dto.request.RouteRequestDto;
import com.digicompass.backend.controller.mapper.RouteMapperController;
import com.digicompass.backend.types.RouteType;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
@Slf4j
public class RouteController {

    private final RouteService routeService;
    private final RouteMapperController routeMapper;

    @Autowired
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
    public ResponseEntity<RouteGeometryDto> getRouteGeometry(@PathVariable Long id) throws JsonMappingException {
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

    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RouteDto> createRoute(@AuthenticationPrincipal UserPrincipal principal, @ModelAttribute RouteRequestDto routeDto) {
        try {
//            routeService.saveRoute(routeMapper.toDomain(routeDto), routeDto.getImages());
            routeService.saveRoute(routeMapper.toDomain(routeDto),routeDto.getImages(), principal.getId());

            log.info("[CONTROLLER] Created route.");
            return ResponseEntity.status(HttpStatus.CREATED).body(routeMapper.toControllerRouteDto(routeDto));
        } catch (Exception e) {
            log.error("[CONTROLLER] Failed to create route", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        try {
            routeService.deleteRoute(id, principal);
            log.info("[CONTROLLER] Deleted route with id {}.", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("[CONTROLLER] Error deleting route with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
