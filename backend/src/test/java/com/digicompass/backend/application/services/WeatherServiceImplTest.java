package com.digicompass.backend.application.services;

import com.digicompass.backend.application.interfaces.WeatherScoringService;
import com.digicompass.backend.application.mapper.WeatherMapper;
import com.digicompass.backend.application.models.weather.CurrentWeather;
import com.digicompass.backend.application.models.weather.DailyWeather;
import com.digicompass.backend.application.models.weather.HourlyWeather;
import com.digicompass.backend.infrastructure.objects.weather.CurrentWeatherObject;
import com.digicompass.backend.infrastructure.objects.weather.DailyWeatherObject;
import com.digicompass.backend.infrastructure.interfaces.WeatherClient;
import com.digicompass.backend.infrastructure.objects.weather.HourlyWeatherObject;
import com.digicompass.backend.repository.cache.interfaces.WeatherCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {

    @Mock
    private WeatherClient client;

    @Mock
    private WeatherCacheRepository weatherCacheRepository;

    @Mock
    private WeatherScoringService scoringService;

    @Mock
    private WeatherMapper weatherMapper;


    @InjectMocks
    private WeatherServiceImpl weatherService;

    private final double lat = 52.3676;
    private final double lon = 4.9041;

    @Test
    void fetchWeatherHourly_CacheMiss_Success() {


        HourlyWeatherObject entity = new HourlyWeatherObject();
        entity.setTime(List.of("2024-01-01T00:00"));
        entity.setTemperature(List.of(10.5));
        entity.setWeatherCode(List.of(0));
        entity.setUvIndex(List.of(2));
        entity.setPrecipitation(List.of(20));

        when(client.getHourly(lat, lon)).thenReturn(entity);

        HourlyWeather mappedWeather = new HourlyWeather();
        when(weatherMapper.toHourlyObject(entity)).thenReturn(mappedWeather);

        HourlyWeather result = weatherService.fetchWeatherHourly(lat, lon);

        assertNotNull(result);
        assertEquals(mappedWeather, result);

    }


    @Test
    void fetchWeatherDaily_CacheMiss_Success() throws Exception {

        DailyWeatherObject entity = new DailyWeatherObject();
        entity.setTime(List.of("2024-01-01T00:00"));
        entity.setMaxTemperature(List.of(15.0));
        entity.setWeatherCode(List.of(0));
        entity.setMinTemperature(List.of(5.0));
        entity.setPrecipitation(List.of(20));

        when(client.getDaily(lat, lon)).thenReturn(entity);

        DailyWeather mappedWeather = new DailyWeather();

        when(weatherMapper.toDailyObject(entity)).thenReturn(mappedWeather);

        DailyWeather result = weatherService.fetchWeatherDaily(lat, lon);

        assertNotNull(result);
        assertEquals(mappedWeather, result);
    }


    @Test
    void fetchWeatherCurrent_CacheMiss_Success(){
        String cacheKey = "weather:current:" + lat + ":" + lon;

        CurrentWeatherObject entity = new CurrentWeatherObject();
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
        when(weatherMapper.toCurrentObject(entity)).thenReturn(mappedWeather);

        CurrentWeather result = weatherService.fetchWeatherCurrent(lat, lon);

        assertNotNull(result);
        assertEquals(mappedWeather, result);

    }

    @Test
    void fetchWeatherHourly_ApiError_ThrowsException() {


        when(client.getHourly(lat, lon)).thenThrow(new RuntimeException("API Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.fetchWeatherHourly(lat, lon));
        assertTrue(ex.getMessage().contains("Error fetching hourly weather"));
    }

    @Test
    void fetchWeatherDaily_ApiError_ThrowsException() {


        when(client.getDaily(lat, lon)).thenThrow(new RuntimeException("API Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.fetchWeatherDaily(lat, lon));
        assertTrue(ex.getMessage().contains("Error fetching daily weather"));
    }

    @Test
    void fetchWeatherCurrent_ApiError_ThrowsException() {


        when(client.getCurrent(lat, lon)).thenThrow(new RuntimeException("API Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> weatherService.fetchWeatherCurrent(lat, lon));
        assertTrue(ex.getMessage().contains("Error fetching current weather"));
    }


}
