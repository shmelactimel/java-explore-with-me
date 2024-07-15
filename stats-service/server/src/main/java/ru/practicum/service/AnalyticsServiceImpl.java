package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HitRequestDto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.HitResponseDto;
import ru.practicum.model.App;
import ru.practicum.model.Request;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final RequestRepository requestRepository;
    private final AppRepository appRepository;
    private final RequestMapper mapper;

    public void addRequest(HitRequestDto hitRequestDto) {
        Optional<App> optionalApp = appRepository.findByName(hitRequestDto.getApp());

        App app = optionalApp.orElseGet(() -> appRepository.save(new App(hitRequestDto.getApp())));

        Request request = mapper.toRequest(hitRequestDto);
        request.setApp(app);
        requestRepository.save(request);
    }

    public List<HitResponseDto> getRequestsWithViews(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return requestRepository.getUniqueIpRequestsWithoutUri(start, end);
            }
            return requestRepository.getUniqueIpRequestsWithUri(start, end, uris);
        } else {
            if (uris == null || uris.isEmpty()) {
                return requestRepository.getAllRequestsWithoutUri(start, end);
            }
            return requestRepository.getAllRequestsWithUri(start, end, uris);
        }
    }
}
