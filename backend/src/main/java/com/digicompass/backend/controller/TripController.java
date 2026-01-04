package com.digicompass.backend.controller;

import com.digicompass.backend.application.interfaces.TripService;
import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.controller.dto.request.TripRequestDto;
import com.digicompass.backend.controller.mapper.TripMapperController;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trip")
@Slf4j
public class TripController {
    private final TripService tripService;
    private final TripMapperController tripMapperController;

    public TripController(TripService tripService, TripMapperController tripMapperController) {
        this.tripService = tripService;
        this.tripMapperController = tripMapperController;
    }

    @PostMapping
    public void createTrip(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Valid TripRequestDto tripRequestDto) {
        tripService.createTrip(tripMapperController.toModel(tripRequestDto), principal.getId());
        log.info("[CONTROLLER] Trip created with name: {}", tripRequestDto.getName());
    }
}
