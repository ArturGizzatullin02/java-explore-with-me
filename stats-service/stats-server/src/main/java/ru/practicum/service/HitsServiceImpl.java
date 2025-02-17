package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.GetHitsRequestParametersDto;
import ru.practicum.HitsDto;
import ru.practicum.PostHitsDto;
import ru.practicum.model.Hits;
import ru.practicum.repository.HitsJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HitsServiceImpl implements HitsService {

    private final HitsJpaRepository hitsRepository;

    private final ModelMapper mapper;

    @Override
    public void saveHits(PostHitsDto postHitsDto) {
        log.info("saveHits for {} started", postHitsDto.getUri());
        Hits hits = mapper.map(postHitsDto, Hits.class);
        LocalDateTime now = LocalDateTime.now();
        hits.setTimestamp(now);
        Hits savedHits = hitsRepository.save(hits);
        log.info("saveHits for {} finished", savedHits.getUri());
    }

    @Override
    public Collection<HitsDto> getHits(GetHitsRequestParametersDto parameters) {

        boolean unique = parameters.isUnique();
        Collection<String> uris = parameters.getUris();
        LocalDateTime start = parameters.getStart();
        LocalDateTime end = parameters.getEnd();

        log.info("started getHits with unique {} for {} from {} to {}", unique, uris, start, end);
        Collection<Hits> hits;
        Collection<HitsDto> hitsDto;
        if (uris == null || uris.isEmpty()) {
            hits = hitsRepository.findAllByTimestampBetween(start, end);
        } else if (unique) {
            hits = hitsRepository.findAllByTimestampBetweenAndUriIn(start, end, uris);
            hits = new ArrayList<>(hits.stream()
                    .collect(Collectors.toMap(Hits::getIp, hit -> hit, (existing, replacement) -> existing))
                    .values());
        } else {
            hits = hitsRepository.findAllByTimestampBetweenAndUriIn(start, end, uris);
        }

        Map<String, String> uriForApp = hits.stream()
                .collect(Collectors.toMap(Hits::getUri, Hits::getApp, (existing, replacement) -> existing));

        Map<String, Long> hitsForUri = hits.stream()
                .collect(Collectors.groupingBy(Hits::getUri, Collectors.counting()));

        hitsDto = hitsForUri.entrySet().stream()
                .map(entry -> HitsDto.builder()
                        .app(uriForApp.get(entry.getKey()))
                        .uri(entry.getKey())
                        .hits(entry.getValue().intValue())
                        .build())
                .sorted((o1, o2) -> Integer.compare(o2.getHits(), o1.getHits()))
                .collect(Collectors.toList());
        log.info("getHits for {} finished", hits);
        return hitsDto;
    }
}
