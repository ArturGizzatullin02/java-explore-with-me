package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.RequestAlreadyConfirmedException;
import ru.practicum.exception.RequestAlreadyExistsException;
import ru.practicum.exception.RequestByInitiatorException;
import ru.practicum.exception.RequestForLimitReachedEventException;
import ru.practicum.exception.RequestForPendingEventException;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequestStatus;
import ru.practicum.model.Request;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(long userId) {
        log.info("getRequestsOfCurrentUser for {} started", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
        List<Request> requestsByRequesterId = requestRepository.findByRequester(userId);
        List<ParticipationRequestDto> result = requestsByRequesterId.stream()
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();
        log.info("getRequestsOfCurrentUser for {} finished", userId);
        return result;
    }

    @Override
    public ParticipationRequestDto saveRequestOfCurrentUser(long userId, long eventId) {
        log.info("saveRequestOfCurrentUser for user {} and event {} started", userId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id %d not found", eventId)));
        Request request;

        if (!event.getRequestModeration()) {
            if ((event.getParticipantLimit() < event.getConfirmedRequests() + 1)) {
                throw new RequestForLimitReachedEventException(String
                        .format("Request for limit reached for event %d", eventId));
            }

            request = Request.builder()
                    .requester(userId)
                    .event(eventId)
                    .created(LocalDateTime.now())
                    .status(ParticipationRequestStatus.CONFIRMED)
                    .build();

            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else if (event.getParticipantLimit() == 0) {
            request = Request.builder()
                    .requester(userId)
                    .event(eventId)
                    .created(LocalDateTime.now())
                    .status(ParticipationRequestStatus.CONFIRMED)
                    .build();
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request = Request.builder()
                    .requester(userId)
                    .event(eventId)
                    .created(LocalDateTime.now())
                    .status(ParticipationRequestStatus.PENDING)
                    .build();
        }

        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new RequestAlreadyExistsException(String
                    .format("Request by requester %d for event %d already exists", userId, eventId));
        }

        if (event.getInitiator().getId() == userId) {
            throw new RequestByInitiatorException(String
                    .format("Request by initiator %d for event %d not allowed", userId, eventId));
        }
        if (EventState.PENDING.equals(event.getState())) {
            throw new RequestForPendingEventException(String
                    .format("Request for pending event %d not allowed", eventId));
        }

        Request savedRequest = requestRepository.save(request);
        ParticipationRequestDto result = mapper.map(savedRequest, ParticipationRequestDto.class);
        log.info("saveRequestOfCurrentUser for {} finished", userId);
        return result;
    }

    @Override
    public ParticipationRequestDto cancelRequestOfCurrentUser(long userId, long requestId) {
        log.info("cancelRequestOfCurrentUser for {} {} started", userId, requestId);
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
        Request request = requestRepository.findByIdAndRequester(requestId, userId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Request with id %d not found", requestId)));
        if (ParticipationRequestStatus.CONFIRMED.equals(request.getStatus())) {
            throw new RequestAlreadyConfirmedException(String.format("Request with id %d already confirmed", requestId));
        }
        request.setStatus(ParticipationRequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        ParticipationRequestDto result = mapper.map(savedRequest, ParticipationRequestDto.class);
        log.info("cancelRequestOfCurrentUser for user {} and request {} finished", userId, requestId);
        return result;
    }
}
