package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class AnalyticsClient {
    private final WebClient webClient;

    public AnalyticsClient(@Value("${stats.server.url}") String statsServerUrl) {
        webClient = WebClient.create(statsServerUrl);
    }

    public void addRequest(HitRequestDto hitRequestDto) {
        webClient.post().uri("/hit").bodyValue(hitRequestDto).retrieve().bodyToMono(Object.class).block();
    }

    public ResponseEntity<List<HitResponseDto>> getStats(String start,
                                                         String end,
                                                         List<String> uris,
                                                         Boolean unique) {

        ResponseEntity<List<HitResponseDto>> listResponseEntity = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end);
                    if (uris != null)
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    if (unique != null)
                        uriBuilder.queryParam("unique", unique);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntityList(HitResponseDto.class)
                .block();
        return listResponseEntity;
    }

    public ResponseEntity<List<HitResponseDto>> getStatsByIp(String start,
                                                               String end,
                                                               List<String> uris,
                                                               Boolean unique,
                                                               String ip) {

        ResponseEntity<List<HitResponseDto>> listResponseEntity = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/statsByIp")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("ip", ip);
                    if (uris != null)
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    if (unique != null)
                        uriBuilder.queryParam("unique", unique);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntityList(HitResponseDto.class)
                .block();
        return listResponseEntity;
    }
}
