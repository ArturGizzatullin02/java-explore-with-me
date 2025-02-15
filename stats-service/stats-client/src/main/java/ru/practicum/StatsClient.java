package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    @Value("${STATS_CLIENT_BASE_URL}")
    private static String BASE_URL;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final RestTemplate restTemplate;

    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    public void sendHit(String app, String uri, String ip) {
        PostHitsDto postHitsDto = PostHitsDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL + "/hit", postHitsDto, Void.class);
        System.out.println("POST /hit response status: " + response.getStatusCode());
    }

    public List<HitsDto> getStats(GetHitsRequestParametersDto parameters) {
        log.info("Start get stats for parameters {}", parameters);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/stats")
                .queryParam("start", parameters.getStart().format(FORMATTER))
                .queryParam("end", parameters.getEnd().format(FORMATTER));

        Collection<String> uris = parameters.getUris();

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        builder.queryParam("unique", parameters.isUnique());

        String url = builder.toUriString();

        log.info("Request URL before exchange: {}", builder.toUriString());
        ResponseEntity<List<HitsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<HitsDto>>() {
                }
        );

        List<HitsDto> stats = response.getBody();
        log.info("Finished get stats for parameters {} with result {}", parameters, stats);
        return stats;
    }
}