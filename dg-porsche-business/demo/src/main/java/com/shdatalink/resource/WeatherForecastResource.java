package com.shdatalink.resource;

import com.shdatalink.service.CacheService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Path("/weather")
public class WeatherForecastResource {

    @Inject
    CacheService cacheService;

    @GET
    public WeatherForecast getForecast(@RestQuery String city, @RestQuery long daysInFuture) {
        long executionStart = System.currentTimeMillis();
        List<String> dailyForecasts = Arrays.asList(
                cacheService.getDailyForecast(LocalDate.now().plusDays(daysInFuture), city),
                cacheService.getDailyForecast(LocalDate.now().plusDays(daysInFuture + 1L), city),
                cacheService.getDailyForecast(LocalDate.now().plusDays(daysInFuture + 2L), city));
        long executionEnd = System.currentTimeMillis();
        return new WeatherForecast(dailyForecasts, executionEnd - executionStart);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class WeatherForecast {

        private List<String> dailyForecasts;

        private long executionTimeInMs;
    }


}
