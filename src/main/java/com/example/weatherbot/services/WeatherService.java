package com.example.weatherbot.services;

import com.example.weatherbot.model.WeatherResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final String apiKey = "595991989859052acee5ef0b3bb9cc94";
    private final String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    @Cacheable("weather")
    public String getWeather(String city) {
        String url = String.format(apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();

        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            if (response != null) {
                return String.format("Температура: %s°C, Ощущаемая: %s°C, Погода: %s, Влажность: %s%%, Скорость ветра: %s м/с",
                        response.getMain().getTemp(),
                        response.getMain().getFeels_like(),
                        response.getWeather().get(0).getDescription(),
                        response.getMain().getHumidity(),
                        response.getWind().getSpeed());
            } else {
                return "Не удалось получить данные о погоде.";
            }
        } catch (RestClientException e) {
            return "Ошибка при обращении к API погоды: " + e.getMessage();
        }
    }
}