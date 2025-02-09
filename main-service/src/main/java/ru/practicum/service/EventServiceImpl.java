package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.GetEventParametersAdminRequest;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ModelMapper mapper;

    @Override
    public List<EventFullDto> getEventsByParams(GetEventParametersAdminRequest parameters) {
        log.info("getEventsByParams for {} started", parameters);
        PageRequest page = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());

        if (parameters.getUsers() == null && parameters.getStates() == null && parameters.getCategories() == null
                && parameters.getRangeStart() == null && parameters.getRangeEnd() == null) {
            Page<Event> events = eventRepository.findAll(page);
            List<EventFullDto> result = events.stream()
                    .map(event -> mapper.map(event, EventFullDto.class))
                    .toList();
            log.info("getEventsByParams for {} ended", parameters);
            return result;
        } else if ()

        return List.of();
    }

    @Override
    public EventFullDto patchEvent(long eventId, UpdateEventAdminRequest event) {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size) {
        return List.of();
    }

    @Override
    public EventFullDto saveEvent(long userId, NewEventDto event) {
        return null;
    }

    @Override
    public EventFullDto getEventFullInfoByCurrentUser(long userId, long eventId) {
        return null;
    }

    @Override
    public EventFullDto patchEventOfCurrentUser(long userId, long eventId, UpdateEventUserRequest event) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByCurrentUserEvents(long userId, long eventId) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult patchParticipationRequestOfCurrentUserEvents(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest statusUpdateRequest
    ) {
        return null;
    }
}
