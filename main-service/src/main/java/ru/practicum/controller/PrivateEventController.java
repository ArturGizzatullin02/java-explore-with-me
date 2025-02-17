package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByCurrentUser(@PathVariable long userId,
                                                      @RequestParam(required = false, defaultValue = "0") int from,
                                                      @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("getEventsByCurrentUser for {} started", userId);
        List<EventShortDto> eventShortDtos = eventService.getEventsByCurrentUser(userId, from, size);
        log.info("getEventsByCurrentUser for {} finished", eventShortDtos);
        return eventShortDtos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("createEvent for {} started for user {}", newEventDto, userId);
        EventFullDto eventFullDto = eventService.saveEvent(userId, newEventDto);
        log.info("createEvent for {} finished", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventFullInfoByCurrentUser(@PathVariable long userId, @PathVariable long eventId) {
        log.info("getEventFullInfoByCurrentUser for {} started", eventId);
        EventFullDto eventFullDto = eventService.getEventFullInfoByCurrentUser(userId, eventId);
        log.info("getEventFullInfoByCurrentUser for {} finished", eventFullDto);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto editEventOfCurrentUser(@PathVariable long userId, @PathVariable long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest updateEventDto) {
        log.info("editEventOfCurrentUser for {} started", eventId);
        EventFullDto eventFullDto = eventService.editEvent(userId, eventId, updateEventDto);
        log.info("editEventOfCurrentUser for {} finished", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsOfCurrentUserEvents(@PathVariable long userId,
                                                                                     @PathVariable long eventId) {
        log.info("getParticipationRequestsByCurrentUser for {} started", eventId);
        List<ParticipationRequestDto> participationRequestDto = eventService
                .getParticipationRequestsByCurrentUserEvents(userId, eventId);
        log.info("getParticipationRequestsByCurrentUser for {} finished", participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchParticipationRequestOfCurrentUserEvents(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("patchParticipationRequestOfCurrentUser for {} started", eventId);
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = eventService
                .patchParticipationRequestOfCurrentUserEvents(userId, eventId, eventRequestStatusUpdateRequest);
        log.info("patchParticipationRequestOfCurrentUser for {} finished", eventRequestStatusUpdateResult);
        return eventRequestStatusUpdateResult;
    }
}
