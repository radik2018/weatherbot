package com.example.weatherbot.services;

import com.example.weatherbot.logs.RequestLog;
import com.example.weatherbot.repositories.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestLogService {
    @Autowired
    private RequestLogRepository logRepository;

    public void logRequest(Long userId, String command, String response) {
        RequestLog log = new RequestLog();
        log.setUserId(userId);
        log.setCommand(command);
        log.setRequestTime(LocalDateTime.now());
        log.setResponse(response);
        logRepository.save(log);
    }

    public List<RequestLog> getAllLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return logRepository.findAll(pageable).getContent();
    }

    public List<RequestLog> getLogsByUserId(Long userId) {
        return logRepository.findByUserId(userId);
    }
}