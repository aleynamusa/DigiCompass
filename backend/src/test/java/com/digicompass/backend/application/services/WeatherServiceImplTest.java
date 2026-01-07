package com.digicompass.backend.application.services;

import com.digicompass.backend.application.mapper.WeatherMapper;
import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;
import com.digicompass.backend.repository.interfaces.WeatherClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private WeatherClient client;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private WeatherMapper weatherMapper;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    private final double lat = 52.3676;
    private final double lon = 4.9041;

    @BeforeEach
    void setup() {
        // Mock redisTemplate.opsForValue() to return our mocked valueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    private <T> void mockCacheHit(String cacheKey, T cachedValue) {
        when(valueOperations.get(cacheKey)).thenReturn(cachedValue);
    }

    private <T> void mockCacheMiss(String cacheKey) {
        when(valueOperations.get(cacheKey)).thenReturn(null);
    }

    @Test
    void fetchWeatherHourly_CacheHit() {
        String cacheKey = "weather:hourly:" + lat + ":" + lon;
        HourlyWeather cached = new HourlyWeather();
        mockCacheHit(cacheKey, cached);

        HourlyWeather result = weatherService.fetchWeatherHourly(lat, lon);

        assertNotNull(result);
        assertEquals(cached, result);
        verify(valueOperations).get(cacheKey);
        verify(client, never()).getHourly(anyDouble(), anyDouble());
    }

    @Test
    void fetchWeatherHourly_CacheMiss_Success() {
        String cacheKey = "weather:hourly:" + lat + ":" + lon;
        mockCacheMiss(cacheKey);

        HourlyWeatherEntity entity = new HourlyWeatherEntity();
        entity.setTime(List.of("2024-01-01T00:00"));
        entity.setTemperature(List.of(10.5));
        entity.setWeatherCode(List.of(0));
        entity.setUvIndex(List.of(2));
        entity.setPrecipitation(List.of(20));

        when(client.getHourly(lat, lon)).thenReturn(entity);

        HourlyWeather mappedWeather = new HourlyWeather();
        when(weatherMapper.toHourly(entity)).thenReturn(mappedWeather);

        HourlyWeather result = weatherService.fetchWeatherHourly(lat, lon);

        assertNotNull(result);
        assertEquals(mappedWeather, result);

        verify(valueOperations).set(cacheKey, mappedWeather, Duration.ofHours(1));
    }


    @Test
    void fetchWeatherDaily_CacheHit() {
        String cacheKey = "weather:daily:" + lat + ":" + lon;
        DailyWeather cached = new DailyWeather();
        mockCacheHit(cacheKey, cached);

        DailyWeather result = weatherService.fetchWeatherDaily(lat, lon);

        assertNotNull(result);
        assertEquals(cached, result);
        verify(valueOperations).get(cacheKey);
        verify(client, never()).getDaily(anyDouble(), anyDouble());
    }

    @Test
    void fetchWeatherDaily_CacheMiss_Success() throws Exception {
        String cacheKey = "weather:daily:" + lat + ":" + lon;
        mockCacheMiss(cacheKey);

        DailyWeatherEntity entity = new DailyWeatherEntity();
        entity.setTime(List.of("2024-01-01T00:00"));
        entity.setMaxTemperature(List.of(15.0));
        entity.setWeatherCode(List.of(0));
        entity.setMinTemperature(List.of(5.0));
        entity.setPrecipitation(List.of(20));

        when(client.getDaily(lat, lon)).thenReturn(entity);

        DailyWeather mappedWeather = new DailyWeather();

        when(weatherMapper.toDaily(entity)).thenReturn(mappedWeather);

        DailyWeather result = weatherService.fetchWeatherDaily(lat, lon);

        assertNotNull(result);
        assertEquals(mappedWeather, result);
        verify(valueOperations).set(cacheKey, mappedWeather, Duration.ofDays(1));
    }

    @Test
    void fetchWeatherCurrent_CacheHit() {
        String cacheKey = "weather:current:" + lat + ":" + lon;
        CurrentWeather cached = new CurrentWeather();
        mockCacheHit(cacheKey, cached);

        CurrentWeather result = weatherService.fetchWeatherCurrent(lat, lon);

        assertNotNull(result);
        assertEquals(cached, result);
        verify(valueOperations).get(cacheKey);
        verify(client, never()).getCurrent(anyDouble(), anyDouble());
    }

    @Test
    void fetchWeatherCurrent_CacheMiss_Success(){
        String cacheKey = "weather:current:" + lat + ":" + lon;
        mockCacheMiss(cacheKey);

        CurrentWeatherEntity entity = new CurrentWeatherEntity();
        entity.setTime("2024-01-01T00:00");
        entity.setTemperature(10.5);
        entity.setWeatherCode(1);
        entity.setHumidity(75);
        entity.setPrecipitation(0.0);
        entity.setFeelsLike(10.0);
        entity.setRain(0.0);
        entity.setCloudCover(50);
        entity.setWindGusts(20.0);
        entity.setWindSpeed(15.0);
        entity.setPrecipitation(0.0);

        when(client.getCurrent(lat, lon)).thenReturn(entity);

        when(client.getCurrent(lat, lon)).thenReturn(entity);

        CurrentWeather mappedWeather = new CurrentWeather();
        when(weatherMapper.toCurrent(entity)).thenReturn(mappedWeather);

        CurrentWeather result = weatherService.fetchWeatherCurrent(lat, lon);

        assertNotNull(result);
        assertEquals(mappedWeather, result);
        verify(valueOperations).set(cacheKey, mappedWeather, Duration.ofMinutes(15



        ));
    }

    @Test
    void fetchWeatherHourly_ApiError_ThrowsException() {
        String cacheKey = "weather:hourly:" + lat + ":" + lon;
        mockCacheMiss(cacheKey);

        when(client.getHourly(lat, lon)).thenThrow(new RuntimeException("API Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.fetchWeatherHourly(lat, lon));
        assertTrue(ex.getMessage().contains("Error fetching hourly weather"));
    }

    @Test
    void fetchWeatherDaily_ApiError_ThrowsException() {
        String cacheKey = "weather:daily:" + lat + ":" + lon;
        mockCacheMiss(cacheKey);

        when(client.getDaily(lat, lon)).thenThrow(new RuntimeException("API Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.fetchWeatherDaily(lat, lon));
        assertTrue(ex.getMessage().contains("Error fetching daily weather"));
    }

    @Test
    void fetchWeatherCurrent_ApiError_ThrowsException() {
        String cacheKey = "weather:current:" + lat + ":" + lon;
        mockCacheMiss(cacheKey);

        when(client.getCurrent(lat, lon)).thenThrow(new RuntimeException("API Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.fetchWeatherCurrent(lat, lon));
        assertTrue(ex.getMessage().contains("Error fetching current weather"));
    }


}
