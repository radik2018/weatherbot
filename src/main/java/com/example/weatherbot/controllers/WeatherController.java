package com.example.weatherbot.controllers;

import com.example.weatherbot.services.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Weather API", description = "API для получения информации о погоде")
public record WeatherController(WeatherService weatherService) {

    @Autowired
    public WeatherController {
    }

    @GetMapping("/weather")
    @Operation(summary = "Получить информацию о погоде", description = "Возвращает текущую погоду для заданного города")
    public ResponseEntity<String> getWeather(
            @Parameter(description = "Название города для получения информации о погоде", required = true)
            @RequestParam String city) {

        String weatherInfo = weatherService.getWeather(city);
        if (weatherInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(weatherInfo);
    }

    @GetMapping("/setcity")
    @Operation(summary = "Установить город по умолчанию", description = "Устанавливает город для получения погоды по умолчанию")
    public ResponseEntity<String> setCity(
            @Parameter(description = "Название города для установки по умолчанию", required = true)
            @RequestParam String city) {


        return ResponseEntity.ok("Город по умолчанию установлен на " + city);
    }
}