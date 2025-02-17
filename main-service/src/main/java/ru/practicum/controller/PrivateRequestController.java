package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable long userId) {
        log.info("getRequests for {} started", userId);
        List<ParticipationRequestDto> participationRequestDto = requestService.getRequestsOfCurrentUser(userId);
        log.info("getRequests for {} finished", participationRequestDto);
        return participationRequestDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable long userId, @RequestParam long eventId) {
        log.info("createRequest for user {} and event {} started", userId, eventId);
        ParticipationRequestDto participationRequestDto = requestService.saveRequestOfCurrentUser(userId, eventId);
        log.info("createRequest for user {} and event {} finished, requestId {}", userId, eventId, participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("cancelRequest for {} {} started", userId, requestId);
        ParticipationRequestDto participationRequestDto = requestService.cancelRequestOfCurrentUser(userId, requestId);
        log.info("cancelRequest for {} {} finished", userId, participationRequestDto);
        return participationRequestDto;
    }
}
