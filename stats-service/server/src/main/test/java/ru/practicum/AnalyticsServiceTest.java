package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.mapper.RequestMapperImpl;
import ru.practicum.model.App;
import ru.practicum.model.Request;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.AnalyticsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
public class AnalyticsServiceTest {

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private AppRepository appRepository;

    @Spy
    private RequestMapperImpl requestMapper;

    HitRequestDto hitRequestDto;

    HitResponseDto hitResponseDto;
    Request request;
    App app;

    @BeforeEach
    void setup() {
        request = new Request(1, new App("main-service"), "/events/1", "192.163.0.1",  LocalDateTime.now());
        hitRequestDto = new HitRequestDto(1, "main-service", "/events/1", "192.163.0.1", LocalDateTime.now());
        hitResponseDto = new HitResponseDto("main-service", "/events/1", 1L);
        app = new App(hitRequestDto.getApp());
    }

    @Test
    void addRequest() {
        Mockito.when(appRepository.findByName(hitRequestDto.getApp()))
                .thenReturn(Optional.ofNullable(app));

        Mockito.when(requestRepository.save(any()))
                .thenReturn(request);

        analyticsService.addRequest(hitRequestDto);

        verify(requestRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void getRequestsWithViews_WhenUnique() {
        Mockito.when(requestRepository.getUniqueIpRequestsWithUri(any(), any(), any()))
                .thenReturn(List.of(hitResponseDtoo));

        List<HitResponseDto> hitResponseDtoSaved = analyticsService.getRequestsWithViews(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), List.of("/events/1"), true);

        assertAll(
                () -> assertEquals(hitResponseDtoSaved.size(), 1),
                () -> assertEquals(hitResponseDtoSaved.get(0).getUri(), request.getUri()),
                () -> assertEquals(hitResponseDtoSaved.get(0).getApp(), app.getName()),
                () -> assertEquals(hitResponseDtoSaved.get(0).getHits(), 1L)
        );

        verify(requestRepository, atMostOnce()).saveAndFlush(any());
    }

    @Test
    void getRequestsWithViews_WhenNotUnique() {
        Mockito.when(requestRepository.getAllRequestsWithUri(any(), any(), any()))
                .thenReturn(List.of(hitResponseDto));

        List<HitResponseDto> hitResponseDtoSaved = analyticsService.getRequestsWithViews(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), List.of("/events/1"), false);

        assertAll(
                () -> assertEquals(hitResponseDtoSaved.size(), 1),
                () -> assertEquals(hitResponseDtoSaved.get(0).getUri(), request.getUri()),
                () -> assertEquals(hitResponseDtoSaved.get(0).getApp(), app.getName()),
                () -> assertEquals(hitResponseDtoSaved.get(0).getHits(), 1L)
        );

        verify(requestRepository, atMostOnce()).saveAndFlush(any());
    }
}
