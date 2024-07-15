package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.HitRequestDto;
import ru.practicum.HitResponseDto;
import ru.practicum.mapper.RequestMapperImpl;
import ru.practicum.service.AnalyticsServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"db.name=test"})
public class AnalyticsControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    private RequestMapperImpl requestMapper;

    @MockBean
    private AnalyticsServiceImpl analyticsService;

    @Autowired
    private MockMvc mockMvc;

    private HitRequestDto hitRequestDto;
    private HitResponseDto hitResponseDto;

    @BeforeEach
    void setup() {
        hitRequestDto = new HitRequestDto(1, "ewm-main-service", "/events/1", "192.163.0.1", LocalDateTime.now());
        hitResponseDto = new HitResponseDto("ewm-main-service", "/events/1", 1L);
    }

    @Test
    void addRequest() throws Exception {
        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(analyticsService, times(1)).addRequest(any());
    }

    @Test
    void getStats() throws Exception {
        String startDT = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endDT = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<String> uris = List.of(hitRequestDto.getUri());
        Boolean unique = false;

        when(analyticsService.getRequestsWithViews(any(), any(), any(), any())).thenReturn(List.of(hitResponseDto));

        mockMvc.perform(get("/stats")
                        .param("start", startDT)
                        .param("end", endDT)
                        .param("uris", String.join(",", uris))
                        .param("unique", unique.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].app", is(HitResponseDto.getApp())))
                .andExpect(jsonPath("$.[0].uri", is(HitResponseDto.getUri())))
                .andExpect(jsonPath("$.[0].hits", is(HitResponseDto.getHits().intValue())));
    }

}
