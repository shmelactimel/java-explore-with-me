package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitRequestDto;
import ru.practicum.HitResponseDto;
import ru.practicum.service.AnalyticsServiceImpl;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AnalyticsController {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AnalyticsServiceImpl analyticsService;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addRequest(@Valid @RequestBody HitRequestDto hitRequestDto) {
        log.info("Calling POST: /hit with 'HitRequestDto': {}", hitRequestDto.toString());
        analyticsService.addRequest(hitRequestDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitResponseDto>> getStats(@RequestParam String start,
                                                         @RequestParam String end,
                                                         @RequestParam(required = false) List<String> uris,
                                                         @RequestParam(defaultValue = "false") Boolean unique) {

        log.info("Calling GET: /stats with 'start': {}, 'end': {}, uris: {}, unique: {}", start, end, uris, unique);

        LocalDateTime startDT;
        LocalDateTime endDT;
        try {
            startDT = LocalDateTime.parse(start, DTF);
            endDT = LocalDateTime.parse(end, DTF);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        if(startDT.isAfter(endDT)) {
            return ResponseEntity.badRequest().build();
        }

        List<HitResponseDto> results = analyticsService.getRequestsWithViews(startDT, endDT, uris, unique);
        return ResponseEntity.ok().body(results);
    }

    @GetMapping("/statsByIp")
    public ResponseEntity<List<HitResponsetDto>> statsByIp(@RequestParam String start,
                                                            @RequestParam String end,
                                                            @RequestParam(required = false) List<String> uris,
                                                            @RequestParam(defaultValue = "false") Boolean unique,
                                                            @RequestParam String ip) {

        log.info("Calling GET: /stats with 'start': {}, 'end': {}, uris: {}, unique: {}", start, end, uris, unique);

        LocalDateTime startDT;
        LocalDateTime endDT;
        try {
            startDT = LocalDateTime.parse(start, DTF);
            endDT = LocalDateTime.parse(end, DTF);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        List<HitResponseDto> results = analyticsService.getRequestsWithViewsByIp(startDT, endDT, uris, unique, ip);
        return ResponseEntity.ok().body(results);
    }
}
