package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.HitsDto;
import ru.practicum.PostHitsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class StatsClient {

    private static final String BASE_URL = "http://localhost:9090";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;

    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Отправка данных о хите.
     *
     * @param app Имя приложения.
     * @param uri URI, для которого регистрируется hit (передаётся внешним сервисом).
     * @param ip  IP-адрес клиента (передаётся внешним сервисом).
     */
    public void sendHit(String app, String uri, String ip) {
        // Формируем DTO, используя полученные от внешнего сервиса данные.
        PostHitsDto postHitsDto = PostHitsDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                // Текущее время форматируем согласно паттерну контроллера
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL + "/hit", postHitsDto, Void.class);
        System.out.println("POST /hit response status: " + response.getStatusCode());
    }

    /**
     * Получение статистики.
     *
     * @param start  Начало временного интервала.
     * @param end    Конец временного интервала.
     * @param uris   Список URI для фильтрации (опционально, может быть null).
     * @param unique Флаг для выборки уникальных значений (опционально, может быть null).
     * @return Список статистических данных.
     */
    public List<HitsDto> getStats(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique) {
        // Формируем URL с обязательными параметрами start и end.
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER));

        // Если список uris передан, добавляем его в параметры запроса.
        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        // Добавляем параметр unique, если он не null.
        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        String url = builder.toUriString();

        ResponseEntity<List<HitsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<HitsDto>>() {
                }
        );

        List<HitsDto> stats = response.getBody();
        if (stats != null) {
            stats.forEach(System.out::println);
        }
        return stats;
    }
}