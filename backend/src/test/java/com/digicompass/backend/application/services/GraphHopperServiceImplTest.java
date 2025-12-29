package com.digicompass.backend.application.services;

import com.digicompass.backend.application.models.map.Point;
import com.digicompass.backend.application.models.map.RouteMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GraphHopperServiceImplTest {

    private MockWebServer mockWebServer;


    private GraphHopperServiceImpl service;


    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        String reverseUrl = mockWebServer.url("/").toString();

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl);

        service = new GraphHopperServiceImpl(builder, baseUrl, reverseUrl);

        ReflectionTestUtils.setField(service, "apiKey", "test-key");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void calculateRoute_success() {
        String mockResponse = """
    {
      "paths": [{
        "distance": 1200.0,
        "time": 300000,
        "points": "}_ilFf~cjV??"
      }]
    }
    """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(mockResponse)
                        .addHeader("Content-Type", "application/json")
        );

        List<Point> points = List.of(
                new Point(48.8566, 2.3522),
                new Point(48.8570, 2.3530)
        );

        RouteMap route = service.calculateRoute(points, "walk");

        assertNotNull(route);
        assertEquals(1.2, route.getDistanceKm(), 0.01);
        assertEquals("5m", route.getDurationHour());
    }

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