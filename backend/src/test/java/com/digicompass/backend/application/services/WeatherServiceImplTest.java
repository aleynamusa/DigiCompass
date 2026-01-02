package com.digicompass.backend.application.services;

import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.GeoLocationResponse;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {


        @Mock
        private WebClient.Builder webClientBuilder;

        @Mock
        private WebClient webClientLocation;

        @Mock
        private WebClient webClientWeather;

        @Mock
        private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

        @Mock
        private WebClient.RequestHeadersSpec requestHeadersSpec;

        @Mock
        private WebClient.ResponseSpec responseSpec;

        @Mock
        private RedisTemplate<String, Object> redisTemplate;

        @Mock
        private ValueOperations<String, Object> valueOperations;


        private WeatherServiceImpl weatherService;

        private static final String LOCATION_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search";
        private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/v1/forecast";

    @BeforeEach
    void setUp() {
        lenient().when(webClientBuilder.baseUrl(LOCATION_BASE_URL)).thenReturn(webClientBuilder); //lenient - dont fail the test id stubbing is unused
        lenient().when(webClientBuilder.baseUrl(WEATHER_BASE_URL)).thenReturn(webClientBuilder);
        lenient().when(webClientBuilder.build())
                .thenReturn(webClientLocation)
                .thenReturn(webClientWeather);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        weatherService = new WeatherServiceImpl(
                webClientBuilder,
                LOCATION_BASE_URL,
                WEATHER_BASE_URL,
                redisTemplate
        );
    }


    @Test
        void fetchLocation_Success() {
            // Arrange
            String cityName = "Amsterdam";
            GeoLocationResponse expectedResponse = new GeoLocationResponse();

            when(webClientLocation.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(GeoLocationResponse.class))
                    .thenReturn(Mono.just(expectedResponse));

            // Act
            GeoLocationResponse result = weatherService.fetchLocation(cityName);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);
            verify(webClientLocation).get();
        }

        @Test
        void fetchWeatherHourly_CacheHit() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;
            String cacheKey = "weather:hourly:" + lat + ":" + lon;
            HourlyWeather cachedWeather = new HourlyWeather();

            when(valueOperations.get(cacheKey)).thenReturn(cachedWeather);

            // Act
            HourlyWeather result = weatherService.fetchWeatherHourly(lat, lon);

            // Assert
            assertNotNull(result);
            assertEquals(cachedWeather, result);
            verify(valueOperations).get(cacheKey);
            verify(webClientWeather, never()).get();
        }

        @Test
        void fetchWeatherHourly_CacheMiss_Success() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;
            String cacheKey = "weather:hourly:" + lat + ":" + lon;

            when(valueOperations.get(cacheKey)).thenReturn(null);

            String jsonResponse = """
                    {
                        "hourly": {
                            "time": ["2024-01-01T00:00"],
                            "temperature_2m": [10.5],
                            "weather_code": [0],
                            "uv_index": [2.0],
                            "precipitation_probability": [20]
                        }
                    }
                    """;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.just(rootNode));

            // Act
            HourlyWeather result = weatherService.fetchWeatherHourly(lat, lon);

            // Assert
            assertNotNull(result);
            verify(valueOperations).set(eq(cacheKey), any(HourlyWeather.class), eq(Duration.ofHours(1)));
        }

        @Test
        void fetchWeatherDaily_CacheHit() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;
            String cacheKey = "weather:daily:" + lat + ":" + lon;
            DailyWeather cachedWeather = new DailyWeather();

            when(valueOperations.get(cacheKey)).thenReturn(cachedWeather);

            // Act
            DailyWeather result = weatherService.fetchWeatherDaily(lat, lon);

            // Assert
            assertNotNull(result);
            assertEquals(cachedWeather, result);
            verify(valueOperations).get(cacheKey);
            verify(webClientWeather, never()).get();
        }

        @Test
        void fetchWeatherDaily_CacheMiss_Success() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;
            String cacheKey = "weather:daily:" + lat + ":" + lon;

            when(valueOperations.get(cacheKey)).thenReturn(null);

            String jsonResponse = """
                    {
                        "daily": {
                            "time": ["2024-01-01"],
                            "weather_code": [0],
                            "temperature_2m_max": [15.0],
                            "temperature_2m_min": [5.0],
                            "precipitation_probability_max": [30]
                        }
                    }
                    """;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.just(rootNode));

            // Act
            DailyWeather result = weatherService.fetchWeatherDaily(lat, lon);

            // Assert
            assertNotNull(result);
            verify(valueOperations).set(eq(cacheKey), any(DailyWeather.class), eq(Duration.ofDays(1)));
        }

        @Test
        void fetchWeatherCurrent_CacheHit() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;
            String cacheKey = "weather:current:" + lat + ":" + lon;
            CurrentWeather cachedWeather = new CurrentWeather();

            when(valueOperations.get(cacheKey)).thenReturn(cachedWeather);

            // Act
            CurrentWeather result = weatherService.fetchWeatherCurrent(lat, lon);

            // Assert
            assertNotNull(result);
            assertEquals(cachedWeather, result);
            verify(valueOperations).get(cacheKey);
            verify(webClientWeather, never()).get();
        }

        @Test
        void fetchWeatherCurrent_CacheMiss_Success() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;
            String cacheKey = "weather:current:" + lat + ":" + lon;

            when(valueOperations.get(cacheKey)).thenReturn(null);

            String jsonResponse = """
                    {
                        "current": {
                            "time": "2024-01-01T12:00",
                            "temperature_2m": 12.5,
                            "relative_humidity_2m": 75,
                            "apparent_temperature": 10.0,
                            "rain": 0.0,
                            "showers": 0.0,
                            "snowfall": 0.0,
                            "wind_speed_10m": 15.0,
                            "precipitation": 0.0,
                            "weather_code": 1,
                            "cloud_cover": 50,
                            "wind_gusts_10m": 20.0,
                            "wind_direction_10m": 180
                        }
                    }
                    """;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.just(rootNode));

            // Act
            CurrentWeather result = weatherService.fetchWeatherCurrent(lat, lon);

            // Assert
            assertNotNull(result);
            verify(valueOperations).set(eq(cacheKey), any(CurrentWeather.class), eq(Duration.ofMinutes(15)));
        }

        @Test
        void fetchWeatherHourly_ApiError_ThrowsException() {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;

            when(valueOperations.get(anyString())).thenReturn(null);
            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.error(new RuntimeException("API Error")));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                    weatherService.fetchWeatherHourly(lat, lon)
            );
        }

        @Test
        void fetchWeatherDaily_ApiError_ThrowsException() {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;

            when(valueOperations.get(anyString())).thenReturn(null);
            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.error(new RuntimeException("API Error")));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                    weatherService.fetchWeatherDaily(lat, lon)
            );
        }

        @Test
        void fetchWeatherCurrent_ApiError_ThrowsException() {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;

            when(valueOperations.get(anyString())).thenReturn(null);
            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.error(new RuntimeException("API Error")));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                    weatherService.fetchWeatherCurrent(lat, lon)
            );
        }

        @Test
        void fetchWeatherHourly_RedisException_StillReturnsData() throws Exception {
            // Arrange
            double lat = 52.3676;
            double lon = 4.9041;

            when(valueOperations.get(anyString())).thenThrow(new RuntimeException("Redis connection failed"));

            String jsonResponse = """
                    {
                        "hourly": {
                            "time": ["2024-01-01T00:00"],
                            "temperature_2m": [10.5],
                            "weather_code": [0],
                            "uv_index": [2.0],
                            "precipitation_probability": [20]
                        }
                    }
                    """;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            when(webClientWeather.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                    .thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(JsonNode.class))
                    .thenReturn(Mono.just(rootNode));

            // Act
            HourlyWeather result = weatherService.fetchWeatherHourly(lat, lon);

            // Assert
            assertNotNull(result);
        }
}
