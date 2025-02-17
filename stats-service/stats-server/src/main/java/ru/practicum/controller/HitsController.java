package ru.practicum.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
import ru.practicum.GetHitsRequestParametersDto;
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

    private final Validator validator;

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
        log.info("get unique: {} stats for {} {} {} started", unique, start, end, uris);

        GetHitsRequestParametersDto parameters = GetHitsRequestParametersDto.builder()
                .unique(unique)
                .uris(uris)
                .start(start)
                .end(end)
                .build();

        var violations = validator.validate(parameters);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }


        Collection<HitsDto> hitsDto = hitsService.getHits(parameters);
        log.info("get unique: {} stats for {} {} {} finished", unique, start, end, uris);
        return hitsDto;
    }
}
