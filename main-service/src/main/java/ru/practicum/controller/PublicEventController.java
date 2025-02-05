package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.GetEventParametersUserRequest;
import ru.practicum.model.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getEventsByParams(@RequestParam String text,
                                                 @RequestParam List<Integer> categories,
                                                 @RequestParam Boolean paid,
                                                 @RequestParam LocalDateTime rangeStart,
                                                 @RequestParam LocalDateTime rangeEnd,
                                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam EventSort sort,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {

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

        log.info("getEvents for {} started", parameters);
        List<EventShortDto> eventShortDtos = eventService.getEventsByParams(parameters);
        log.info("getEvents for {} finished", parameters);
        return eventShortDtos;
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long eventId) {
        log.info("getEvent for {} started", eventId);
        EventFullDto eventFullDto = eventService.getEvent(eventId);
        log.info("getEvent for {} finished", eventId);
        return eventFullDto;
    }
}
