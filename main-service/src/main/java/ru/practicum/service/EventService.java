package ru.practicum.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.GetEventParametersAdminRequest;
import ru.practicum.dto.GetEventParametersUserRequest;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsByParams(GetEventParametersAdminRequest parameters);

    List<EventShortDto> getEventsShortDtoByParams(GetEventParametersUserRequest parameters);

    EventFullDto patchEvent(long eventId, UpdateEventAdminRequest event);

    List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size);

    EventFullDto saveEvent(long userId, NewEventDto event);

    EventFullDto getEventFullInfoByCurrentUser(long userId, long eventId);

    EventFullDto patchEvent(long userId, long eventId, UpdateEventUserRequest event);

    List<ParticipationRequestDto> getParticipationRequestsByCurrentUserEvents(long userId, long eventId);

    EventRequestStatusUpdateResult patchParticipationRequestOfCurrentUserEvents(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest statusUpdateRequest
    );

    EventFullDto getPublishedEventFullInfo(long eventId);

    void setViews(long eventId, int views);
}
