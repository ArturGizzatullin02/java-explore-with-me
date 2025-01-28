package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.HitsDto;
import ru.practicum.PostHitsDto;
import ru.practicum.service.HitsService;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HitsController {

    private final HitsService hitsService;

    @PostMapping("/hit")
    public ResponseEntity<Void> hit(@RequestBody PostHitsDto postHitsDto) {
        log.info("hit stats for {} {} started", postHitsDto.getApp(), postHitsDto.getUri());
        hitsService.saveHits(postHitsDto);
        log.info("hit stats for {} {} finished", postHitsDto.getApp(), postHitsDto.getUri());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public Collection<HitsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(required = false) Collection<String> uris,
                                        @RequestParam(required = false) boolean unique) {
        log.info("get stats for {} {} {} started", start, end, uris);
        Collection<HitsDto> hitsDto = hitsService.getHits(start, end, uris, unique);
        log.info("get stats for {} {} {} finished", start, end, uris);
        return hitsDto;
    }
}
