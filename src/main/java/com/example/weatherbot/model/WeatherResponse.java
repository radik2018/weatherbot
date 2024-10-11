package com.example.weatherbot.model;

import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {
    private Main main;
    private List<Weather> weather;
    private Wind wind;

    @Data
    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;
    }

    @Data
    public static class Weather {
        private String description;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}
