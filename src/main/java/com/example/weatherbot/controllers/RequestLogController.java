package com.example.weatherbot.controllers;

import com.example.weatherbot.logs.RequestLog;
import com.example.weatherbot.services.RequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class RequestLogController {

    @Autowired
    private RequestLogService logService;

    @GetMapping
    public List<RequestLog> getAllLogs(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        try {
            return logService.getAllLogs(page, size);
        } catch (Exception e) {

            throw new RuntimeException("Не удалось получить логи: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public List<RequestLog> getLogsByUserId(@PathVariable Long userId) {
        try {
            return logService.getLogsByUserId(userId);
        } catch (Exception e) {

            throw new RuntimeException("Не удалось получить логи пользователя с ID " + userId + ": " + e.getMessage());
        }
    }
}