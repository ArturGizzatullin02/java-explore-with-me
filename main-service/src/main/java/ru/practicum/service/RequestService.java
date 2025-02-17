package ru.practicum.service;

import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestsOfCurrentUser(long userId);

    ParticipationRequestDto saveRequestOfCurrentUser(long userId, long eventId);

    ParticipationRequestDto cancelRequestOfCurrentUser(long userId, long requestId);
}
