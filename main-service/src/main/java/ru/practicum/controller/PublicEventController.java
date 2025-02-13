package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.GetHitsRequestParametersDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.GetEventParametersUserRequest;
import ru.practicum.model.EventSort;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicEventController {

    private final EventService eventService;

    private final Validator validator;

    private final StatsClient statsClient;

    @GetMapping("/events")
    public List<EventShortDto> getEventsByParams(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) EventSort sort,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size,
                                                 HttpServletRequest request) {

//        GetHitsRequestParametersDto hitParameters = GetHitsRequestParametersDto.builder()
//                .start()
//                .end()
//                .uris(List.of(request.getRequestURI()))
//                .build();

//        ResponseEntity<String> stats = statsClient.getStats(hitParameters);

        GetEventParametersUserRequest parameters = GetEventParametersUserRequest.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        var violations = validator.validate(parameters);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        log.info("getEvents for {} started", parameters);
        List<EventShortDto> eventShortDtos = eventService.getEventsShortDtoByParams(parameters);

//        statsClient.hit(request.getRequestURI(), request.getRemoteAddr());

        log.info("getEvents for {} finished", parameters);
        return eventShortDtos;
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getPublishedEventFullInfo(@PathVariable long eventId) {
        log.info("getEvent for {} started", eventId);
        EventFullDto eventFullDto = eventService.getPublishedEventFullInfo(eventId);
        log.info("getEvent for {} finished", eventId);
        return eventFullDto;
    }
}
