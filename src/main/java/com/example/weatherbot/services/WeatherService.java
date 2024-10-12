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
                double temperature = response.getMain().getTemp();
                double feelsLike = response.getMain().getFeels_like();
                String weatherDescription = translateWeatherDescription(response.getWeather().get(0).getDescription());
                int humidity = response.getMain().getHumidity();
                double windSpeed = response.getWind().getSpeed();

                return String.format("Температура: %.2f°C,\nОщущаемая: %.2f°C,\nПогода: %s,\nВлажность: %d%%,\nСкорость ветра: %.2f м/с",
                        temperature, feelsLike, weatherDescription, humidity, windSpeed);
            } else {
                return "Не удалось получить данные о погоде.";
            }
        } catch (RestClientException e) {
            return "Ошибка при обращении к API погоды: " + e.getMessage();
        }
    }

    private String translateWeatherDescription(String description) {
        switch (description.toLowerCase()) {
            case "clear":
                return "Ясно";
            case "clouds":
                return "Облачно";
            case "rain":
                return "Дождь";
            case "snow":
                return "Снег";
            case "mist":
                return "Туман";

            default:
                return description;
        }
    }
}