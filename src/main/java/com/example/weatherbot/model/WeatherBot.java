package com.example.weatherbot.model;

import com.example.weatherbot.config.BotConfig;
import com.example.weatherbot.services.RequestLogService;
import com.example.weatherbot.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeatherBot extends TelegramLongPollingBot {

    final BotConfig config;

    public WeatherBot(BotConfig config) {
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
    private Map<Long, Boolean> waitingForCityInput = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Получено обновление: " + update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();

            if (messageText.equals("/start")) {
                String welcomeMessage = "Добро пожаловать! Этот бот был создан в качестве теста для компании BobrAi.";
                sendButtons(chatId, welcomeMessage);
                return;
            }

            if (waitingForCityInput.getOrDefault(userId, false)) {
                String city = messageText;
                userCities.put(userId, city);
                sendMessage(chatId, "Город " + city + " установлен по умолчанию.");
                waitingForCityInput.put(userId, false);
                sendButtons(chatId, "Выберите действие:");
                return;
            }

            if (messageText.equals("Установить город по умолчанию")) {
                sendMessage(chatId, "Введите название города на английском языке:");
                waitingForCityInput.put(userId, true);
                return;
            }

            if (messageText.equals("Узнать погоду")) {
                String city = userCities.get(userId);
                if (city == null) {
                    sendMessage(chatId, "Пожалуйста, укажите город или установите его командой /setcity.");
                    return;
                }

                String response = weatherService.getWeather(city);
                if (response == null || response.contains("\"cod\":\"404\"")) {
                    response = "Не удалось найти город: " + city + ". Проверьте правильность названия.";
                }

                sendMessage(chatId, response);
                requestLogService.logRequest(userId, "Узнать погоду", response);
            } else {
                sendMessage(chatId, "Неизвестная команда. Используйте /weather <город> или /setcity.");
            }
        }
    }

    private void sendButtons(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(createButtons());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при отправке сообщения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup createButtons() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add("Узнать погоду");
        row.add("Установить город по умолчанию");

        keyboard.add(row);
        markup.setKeyboard(keyboard);

        return markup;
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