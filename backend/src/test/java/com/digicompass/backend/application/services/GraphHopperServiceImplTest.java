package com.digicompass.backend.application.services;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.infrastructure.interfaces.GraphHopperClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GraphHopperServiceImplTest {
    @Mock
    GraphHopperClient graphHopperClient;

    @InjectMocks
    private GraphHopperServiceImpl service;


    @Test
    void calculateRoute_throwsIllegalArgumentException_whenMoreThanThan5Points() {
        List<Point> points = List.of(
                new Point(48.8566, 2.3522),
                new Point(48.8570, 2.3530),
                new Point(48.8580, 2.3540),
                new Point(48.8590, 2.3550),
                new Point(48.8600, 2.3560),
                new Point(48.8610, 2.3570)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateRoute(points, "walk")
        );
    }

}