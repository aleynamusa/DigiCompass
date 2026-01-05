package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.TripService;
import com.digicompass.backend.application.mapper.TripMapper;
import com.digicompass.backend.application.models.Trip;
import com.digicompass.backend.repository.repositories.TripJpaRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripJpaRepository tripJpaRepository;
    private final TripMapper tripMapper;

    @Autowired
    public TripServiceImpl(TripJpaRepository tripJpaRepository, TripMapper tripMapper) {
        this.tripJpaRepository = tripJpaRepository;
        this.tripMapper = tripMapper;
    }

    @Override
    public void createTrip(@Valid Trip trip, Long id) {
        try{

            trip.setUserId(id);
            log.info("[SERVICE] Creating trip {}", trip.getName());
            tripJpaRepository.save(tripMapper.ToEntity(trip));
        }
        catch (Exception e){
            log.error("[SERVICE] Error while creating trip {}", trip.getName(), e);
            throw new RuntimeException("Error while creating trip " + trip.getName(), e);
        }

    }

    @Override
    public List<String> getAllRoutes() {
//        try{
//            log.info("[SERVICE] Getting all routes");
//            routeRepository.
//        }
        return List.of();
    }
}
