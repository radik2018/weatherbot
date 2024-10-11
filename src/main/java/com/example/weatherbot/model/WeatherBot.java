package com.example.weatherbot.model;

import com.example.weatherbot.config.BotConfig;
import com.example.weatherbot.services.RequestLogService;
import com.example.weatherbot.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherBot extends TelegramLongPollingBot {

    final BotConfig config;

    public  WeatherBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private RequestLogService requestLogService;

    private Map<Long, String> userCities = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("Получено обновление: " + update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();

            if (messageText.equals("/start")) {
                String welcomeMessage = "Добро пожаловать! Этот бот был создан в качестве теста для компании BobrAi." + "\n" +
                        "Определить город: /setcity" + "\n" + "Узнать погоду /weather";
                sendMessage(update.getMessage().getChatId(), welcomeMessage);
                return;
            }

            System.out.println("Бот запущен: " + update);

            if (messageText.startsWith("/setcity")) {
                String[] parts = messageText.split(" ");
                if (parts.length < 2) {
                    sendMessage(update.getMessage().getChatId(), "Пожалуйста, укажите город в формате /setcity <название города на английском языке>");
                    return;
                }

                String city = parts[1];
                userCities.put(userId, city);
                sendMessage(update.getMessage().getChatId(), "Город " + city + " установлен по умолчанию.");
                return;
            }

            if (messageText.startsWith("/weather")) {
                String city;

                String[] parts = messageText.split(" ");
                if (parts.length < 2) {

                    city = userCities.get(userId);
                    if (city == null) {
                        sendMessage(update.getMessage().getChatId(), "Пожалуйста, укажите город или установите его командой /setcity <город>.");
                        return;
                    }
                } else {
                    city = parts[1];
                }

                String response = weatherService.getWeather(city);
                if (response == null || response.isEmpty()) {
                    response = "Не удалось получить погоду для города: " + city + ". Проверьте название.";
                }

                sendMessage(update.getMessage().getChatId(), response);
                requestLogService.logRequest(userId, messageText, response);
            } else {
                sendMessage(update.getMessage().getChatId(), "Неизвестная команда. Используйте /weather <город> или /setcity <город>.");
            }
        }
    }

    private void sendMessage(Long chatId, String text) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}