package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.GetEventParametersAdminRequest;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.model.EventState;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsByParams(@RequestParam List<Long> users,
                                                @RequestParam List<EventState> states,
                                                @RequestParam List<Long> categories,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {

        GetEventParametersAdminRequest parameters = GetEventParametersAdminRequest.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        log.info("getEvents for {} started", parameters);
        List<EventFullDto> eventFullDtos = eventService.getEventsByParams(parameters);
        log.info("getEvents for {} finished", parameters);
        return eventFullDtos;
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable long eventId, @RequestBody UpdateEventAdminRequest event) {
        log.info("patchEvent for {} and {} started", eventId, event);
        EventFullDto eventFullDto = eventService.patchEvent(eventId, event);
        log.info("patchEvent for {} and {} finished", eventFullDto.getId(), eventFullDto);
        return eventFullDto;
    }
}
