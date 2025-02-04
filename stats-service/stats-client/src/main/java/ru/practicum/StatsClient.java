package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class StatsClient {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestClient restClient;

    @Value(value = "${STATS_CLIENT_BASE_URL}")
    private String baseUrl;

    public StatsClient() {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private static final String APPLICATION_NAME = "stats-service";

    public ResponseEntity<Void> hit(String uri, String ip) {
        PostHitsDto postHitsDto = PostHitsDto.builder()
                .app(APPLICATION_NAME)
                .uri(uri)
                .ip(ip)
                .build();

        return restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(postHitsDto)
                .retrieve()
                .toBodilessEntity();
    }

    public ResponseEntity<String> getStats(GetHitsRequestParametersDto dto) {
        String start = URLEncoder.encode(dto.getStart().format(formatter), StandardCharsets.UTF_8);
        String end = URLEncoder.encode(dto.getEnd().format(formatter), StandardCharsets.UTF_8);

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", dto.getUris())
                        .queryParam("unique", dto.isUnique())
                        .build())
                .retrieve()
                .toEntity(String.class);
    }
}
