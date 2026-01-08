package com.digicompass.backend.repository.cache;


import com.digicompass.backend.repository.cache.interfaces.WeatherCacheRepository;
import com.digicompass.backend.repository.entity.weather.CurrentWeatherEntity;
import com.digicompass.backend.repository.entity.weather.DailyWeatherEntity;
import com.digicompass.backend.repository.entity.weather.HourlyWeatherEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
public class WeatherCacheRedisRepository implements WeatherCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public WeatherCacheRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<HourlyWeatherEntity> getHourlyWeather(double lat, double lon) {
        String key = buildKey("hourly", lat, lon);
        try {
            HourlyWeatherEntity cached = (HourlyWeatherEntity) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[REPOSITORY] Cache hit for hourly weather at {},{}", lat, lon);
                return Optional.of(cached);
            }
            log.info("[REPOSITORY] Cache miss for hourly weather at {},{}", lat, lon);
            return Optional.empty();
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error getting hourly weather: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void saveHourlyWeather(double lat, double lon, HourlyWeatherEntity weather) {
        String key = buildKey("hourly", lat, lon);
        try {
            redisTemplate.opsForValue().set(key, weather, Duration.ofHours(1));
            log.info("[REPOSITORY] Saved hourly weather to cache for {},{}", lat, lon);
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error saving hourly weather: {}", e.getMessage());
        }
    }

    @Override
    public Optional<DailyWeatherEntity> getDailyWeather(double lat, double lon) {
        String key = buildKey("daily", lat, lon);
        try {
            DailyWeatherEntity cached = (DailyWeatherEntity) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[REPOSITORY] Cache hit for daily weather at {},{}", lat, lon);
                return Optional.of(cached);
            }
            log.info("[REPOSITORY] Cache miss for daily weather at {},{}", lat, lon);
            return Optional.empty();
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error getting daily weather: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void saveDailyWeather(double lat, double lon, DailyWeatherEntity weather) {
        String key = buildKey("daily", lat, lon);
        try {
            redisTemplate.opsForValue().set(key, weather, Duration.ofDays(1));
            log.info("[REPOSITORY] Saved daily weather to cache for {},{}", lat, lon);
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error saving daily weather: {}", e.getMessage());
        }
    }

    @Override
    public Optional<CurrentWeatherEntity> getCurrentWeather(double lat, double lon) {
        String key = buildKey("current", lat, lon);
        try {
            CurrentWeatherEntity cached = (CurrentWeatherEntity) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[REPOSITORY] Cache hit for current weather at {},{}", lat, lon);
                return Optional.of(cached);
            }
            log.info("[REPOSITORY] Cache miss for current weather at {},{}", lat, lon);
            return Optional.empty();
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error getting current weather: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void saveCurrentWeather(double lat, double lon, CurrentWeatherEntity weather) {
        String key = buildKey("current", lat, lon);
        try {
            redisTemplate.opsForValue().set(key, weather, Duration.ofMinutes(15));
            log.info("[REPOSITORY] Saved current weather to cache for {},{}", lat, lon);
        } catch (Exception e) {
            log.error("[REPOSITORY] Redis error saving current weather: {}", e.getMessage());
        }
    }

    private String buildKey(String type, double lat, double lon) {
        return String.format("weather:%s:%.4f:%.4f", type, lat, lon);
    }
}