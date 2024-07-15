package ru.practicum.service;

import ru.practicum.HitRequestDto;
import ru.practicum.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalyticsService {
    void addRequest(HitRequestDto hitRequestDto);

    List<HitResponseDto> getRequestsWithViews(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
