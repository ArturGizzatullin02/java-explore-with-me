package ru.practicum.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.GetHitsRequestParametersDto;
import ru.practicum.HitsDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.GetEventParametersAdminRequest;
import ru.practicum.dto.GetEventParametersBaseRequest;
import ru.practicum.dto.GetEventParametersUserRequest;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventBaseRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.EventAlreadyCanceledException;
import ru.practicum.exception.EventAlreadyPublishedException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.LocationNotFoundException;
import ru.practicum.exception.PermissionDeniedException;
import ru.practicum.exception.RequestAlreadyConfirmedException;
import ru.practicum.exception.RequestForLimitReachedEventException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.model.ParticipationRequestStatus;
import ru.practicum.model.QEvent;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final RequestRepository requestRepository;

    private final Validator validator;

    private final StatsClient statsClient;

    private final ModelMapper mapper;

    @Override
    public List<EventFullDto> getEventsByParams(GetEventParametersAdminRequest parameters) {
        log.info("getEventsByParams for {} started", parameters);
        PageRequest page = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());

        QEvent event = QEvent.event;
        BooleanBuilder predicate = buildBaseGetEventPredicate(parameters);

        if (parameters.getUsers() != null && !parameters.getUsers().isEmpty()) {
            predicate.and(event.initiator.id.in(parameters.getUsers()));
        }

        if (parameters.getStates() != null && !parameters.getStates().isEmpty()) {
            predicate.and(event.state.in(parameters.getStates()));
        }

        Page<Event> events = eventRepository.findAll(predicate, page);
        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventEntity -> mapper.map(eventEntity, EventFullDto.class))
                .toList();
        log.info("getEventsByParams for {} finished", parameters);
        return eventFullDtos;
    }

    @Override
    public List<EventShortDto> getEventsShortDtoByParams(GetEventParametersUserRequest parameters) {
        log.info("getEventsShortDtosByParams for {} started", parameters);
        PageRequest page = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());

        QEvent event = QEvent.event;
        BooleanBuilder predicate = buildBaseGetEventPredicate(parameters);

        if (parameters.getText() != null && !parameters.getText().isBlank()) {
            predicate.and(event.annotation.containsIgnoreCase(parameters.getText())
                    .or(event.description.containsIgnoreCase(parameters.getText())));
        }

        if (parameters.getPaid() != null) {
            predicate.and(event.paid.eq(parameters.getPaid()));
        }

        if (Boolean.TRUE.equals(parameters.getOnlyAvailable())) {
            predicate.and(event.participantLimit.isNull()
                    .or(event.participantLimit.gt(event.confirmedRequests)));
        }

        Page<Event> events = eventRepository.findAll(predicate, page);

        List<String> eventUris = events.getContent().stream()
                .map(eventEntity -> "/events/" + eventEntity.getId())
                .toList();

        GetHitsRequestParametersDto getStatsParameters = GetHitsRequestParametersDto.builder()
                .start(LocalDateTime.now().minusYears(1))
                .end(LocalDateTime.now().plusSeconds(1))
                .uris(eventUris)
                .unique(true)
                .build();

        var violations = validator.validate(getStatsParameters);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        List<HitsDto> hitsDtos = statsClient.getStats(getStatsParameters);

        Map<Long, Integer> viewsMapByIds = hitsDtos.stream()
                .collect(Collectors.toMap(
                        hit -> Long.parseLong(hit.getUri().substring("/events/".length())),
                        HitsDto::getHits,
                        (existing, replacement) -> existing
                ));

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventEntity -> {
                    EventShortDto eventDto = mapper.map(eventEntity, EventShortDto.class);
                    eventDto.setViews(viewsMapByIds.getOrDefault(eventDto.getId(), 0));
                    return eventDto;
                })
                .toList();

        if (parameters.getSort() == null) {
            log.info("getEventsShortDtosByParams for {} finished with sort == null", parameters);
            return eventShortDtos;
        } else {
            List<EventShortDto> sortedEventShortDtos;
            switch (parameters.getSort()) {
                case VIEWS:
                    sortedEventShortDtos = eventShortDtos.stream()
                            .sorted((e1, e2) -> e2.getViews().compareTo(e1.getViews()))
                            .toList();
                    break;
                case EVENT_DATE:
                default:
                    sortedEventShortDtos = eventShortDtos.stream()
                            .sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .toList();
                    break;
            }

            log.info("getEventsShortDtosByParams for {} finished with sort != null", parameters);
            return sortedEventShortDtos;
        }
    }

    private BooleanBuilder buildBaseGetEventPredicate(GetEventParametersBaseRequest parameters) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(parameters.getCategories()));
        }

        if (parameters.getRangeStart() != null) {
            predicate.and(event.eventDate.after(parameters.getRangeStart()));
        }
        if (parameters.getRangeEnd() != null) {
            predicate.and(event.eventDate.before(parameters.getRangeEnd()));
        }
        if (parameters.getRangeStart() == null && parameters.getRangeEnd() == null) {
            predicate.and(event.eventDate.after(LocalDateTime.now()));
        }

        return predicate;
    }

    @Override
    @Transactional
    public EventFullDto editEvent(long eventId, UpdateEventAdminRequest editRequest) {
        log.info("editEvent admin request for event {} started for {}", eventId, editRequest);
        Event eventFromRepository = getEventById(eventId);

        editEventFields(eventFromRepository, editRequest);

        if (editRequest.getStateAction() != null) {
            if (EventState.CANCELED.equals(eventFromRepository.getState())) {
                throw new EventAlreadyCanceledException(String
                        .format("Event %d already canceled", eventId));
            } else if (EventState.PUBLISHED.equals(eventFromRepository.getState())) {
                throw new EventAlreadyCanceledException(String
                        .format("Event %d already published", eventId));
            } else {
                eventFromRepository.setState(editRequest.getStateAction().toEventState());
            }
        }
        if (EventState.PUBLISHED.equals(eventFromRepository.getState())) {
            eventFromRepository.setPublishedOn(LocalDateTime.now());
        }

        Event savedEvent = eventRepository.save(eventFromRepository);
        EventFullDto result = mapper.map(savedEvent, EventFullDto.class);
        log.info("editEvent admin request for event {} finished with result {}", eventId, result);
        return result;
    }

    @Override
    @Transactional
    public EventFullDto editEvent(long userId, long eventId, UpdateEventUserRequest editRequest) {
        log.info("editEvent user request for event {} started for {}", eventId, editRequest);
        Event eventFromRepository = getEventById(eventId);

        editEventFields(eventFromRepository, editRequest);

        if (EventState.PUBLISHED.equals(eventFromRepository.getState())) {
            throw new EventAlreadyPublishedException(String
                    .format("Event %d already published", eventId));
        }

        if (editRequest.getStateAction() != null) {
            eventFromRepository.setState(editRequest.getStateAction().toEventState());
        }

        Event savedEvent = eventRepository.save(eventFromRepository);
        EventFullDto result = mapper.map(savedEvent, EventFullDto.class);
        log.info("editEvent user request for event {} for user {} with result {} finished", result, userId, result);
        return result;
    }

    private void editEventFields(Event eventFromRepository, UpdateEventBaseRequest editRequest) {
        log.info("editEventFields for {} started for {}", eventFromRepository, editRequest);

        if (editRequest.getAnnotation() != null) {
            eventFromRepository.setAnnotation(editRequest.getAnnotation());
        }
        if (editRequest.getCategory() != null) {
            eventFromRepository.setCategory(categoryRepository.findById(editRequest.getCategory())
                    .orElseThrow(() -> new EventNotFoundException("Category not found")));
        }
        if (editRequest.getDescription() != null) {
            eventFromRepository.setDescription(editRequest.getDescription());
        }
        if (editRequest.getEventDate() != null) {
            eventFromRepository.setEventDate(editRequest.getEventDate());
        }
        if (editRequest.getLocation() != null) {
            if (editRequest.getLocation().getId() == null) {
                Location savedLocation = createLocation(editRequest.getLocation());
                eventFromRepository.setLocation(savedLocation);
            } else {
                Location location = locationRepository.findById(editRequest.getLocation().getId())
                        .orElseThrow(() -> new LocationNotFoundException("Location not found"));
                eventFromRepository.setLocation(location);
            }
        }
        if (editRequest.getPaid() != null) {
            eventFromRepository.setPaid(editRequest.getPaid());
        }
        if (editRequest.getParticipantLimit() != null) {
            eventFromRepository.setParticipantLimit(editRequest.getParticipantLimit());
        }
        if (editRequest.getRequestModeration() != null) {
            eventFromRepository.setRequestModeration(editRequest.getRequestModeration());
        }
        if (editRequest.getTitle() != null) {
            eventFromRepository.setTitle(editRequest.getTitle());
        }
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size) {
        log.info("getEventsByCurrentUser for {} started", userId);
        PageRequest page = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventEntity -> mapper.map(eventEntity, EventShortDto.class))
                .toList();
        log.info("getEventsByCurrentUser for {} finished", userId);
        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFullDto saveEvent(long userId, NewEventDto eventDto) {
        log.info("saveEvent for {} for user {} started", eventDto, userId);
        Event event = mapper.map(eventDto, Event.class);
        event.setCreatedOn(LocalDateTime.now());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User %d not found", userId)));
        event.setInitiator(user);
        event.setState(EventState.PENDING);

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(String
                        .format("Category %d not found", eventDto.getCategory())));

        event.setCategory(category);

        Location location = eventDto.getLocation();

        if (location.getId() == null) {
            location = createLocation(location);
        }
        event.setLocation(location);
        event.setConfirmedRequests(0);
        log.info("event for save prepared {}", event);
        Event savedEvent = eventRepository.save(event);
        EventFullDto result = mapper.map(savedEvent, EventFullDto.class);
        log.info("saveEvent for {} for user {} finished", eventDto, userId);
        return result;
    }

    @Override
    public EventFullDto getEventFullInfoByCurrentUser(long userId, long eventId) {
        Event event = getEventById(eventId);
        checkUserPermission(userId, event);
        return mapper.map(event, EventFullDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequestsByCurrentUserEvents(long userId, long eventId) {
        log.info("getParticipationRequestsByCurrentUserEvents for {} started", eventId);
        Event event = getEventById(eventId);

        checkUserPermission(userId, event);

        List<Request> requests = requestRepository.findAllByEvent(eventId);
        List<ParticipationRequestDto> result = requests.stream()
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();
        log.info("getParticipationRequestsByCurrentUserEvents for {} finished", eventId);
        return result;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestOfCurrentUserEvents(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest statusUpdateRequest) {

        log.info("patchParticipationRequestOfCurrentUserEvents for event {} user {} requests {} started",
                eventId, userId, statusUpdateRequest.getRequestIds());
        List<Request> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());

        Event event = getEventById(eventId);

        checkUserPermission(userId, event);

        AtomicInteger confirmedRequests = new AtomicInteger(event.getConfirmedRequests());

        if (RequestStatus.CONFIRMED.equals(statusUpdateRequest.getStatus())) {
            confirmRequests(event, requests, confirmedRequests);
        } else if (RequestStatus.REJECTED.equals(statusUpdateRequest.getStatus())) {
            rejectRequests(requests);
        }

        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.get());
        eventRepository.save(event);

        requestRepository.saveAll(requests);

        EventRequestStatusUpdateResult result = buildResultForRequestStatusUpdate(eventId);

        log.info("event after save: {}", event);
        log.info("patchParticipationRequestOfCurrentUserEvents for event {} user {} requests {} finished",
                eventId, userId, statusUpdateRequest.getRequestIds());
        return result;
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));
    }

    private void checkUserPermission(long userId, Event event) {
        if (event.getInitiator().getId() != userId) {
            throw new PermissionDeniedException(String.format("User %d not allowed to patch event %d", userId, event.getId()));
        }
    }

    private void confirmRequests(Event event, List<Request> requests, AtomicInteger confirmedRequests) {
        requests.forEach(request -> {
            if (!event.getRequestModeration()) {
                if ((event.getParticipantLimit() < confirmedRequests.get() + requests.size())) {
                    throw new RequestForLimitReachedEventException(String
                            .format("Request for limit reached for event %d", event.getId()));
                }
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
            } else if (event.getParticipantLimit() == 0) {
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
            } else {
                if (event.getParticipantLimit() <= confirmedRequests.get()) {
                    log.info("eventConfirmedRequests after LIMIT REACHED save request {}", confirmedRequests.get());

                    log.info("LIMIT REACHED WHEN NO MODERATION AND NO PARTICIPANT LIMIT==0");
                    throw new RequestForLimitReachedEventException(String
                            .format("Request for limit reached for event %d", event.getId()));
                }
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
                confirmedRequests.getAndIncrement();
                log.info("eventConfirmedRequests after save request {}", confirmedRequests.get());
            }
        });
    }

    private void rejectRequests(List<Request> requests) {
        requests.forEach(request -> {
            if (ParticipationRequestStatus.CONFIRMED.equals(request.getStatus())) {
                throw new RequestAlreadyConfirmedException(String
                        .format("Request %d already confirmed", request.getId()));
            }
            request.setStatus(ParticipationRequestStatus.REJECTED);
        });
    }

    private EventRequestStatusUpdateResult buildResultForRequestStatusUpdate(long eventId) {
        List<Request> savedRequests = requestRepository.findAllByEvent(eventId);

        List<ParticipationRequestDto> confirmedRequestsDto;
        List<ParticipationRequestDto> rejectedRequestsDto;

        confirmedRequestsDto = savedRequests.stream()
                .filter(request -> ParticipationRequestStatus.CONFIRMED.equals(request.getStatus()))
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();

        rejectedRequestsDto = savedRequests.stream()
                .filter(request -> ParticipationRequestStatus.REJECTED.equals(request.getStatus()))
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequestsDto)
                .rejectedRequests(rejectedRequestsDto)
                .build();
    }

    private Location createLocation(Location location) {
        log.info("createLocation for {} started", location);
        Location savedLocation = locationRepository.save(location);
        log.info("createLocation for {} finished", location);
        return savedLocation;
    }

    @Override
    public EventFullDto getPublishedEventFullInfo(long eventId) {
        log.info("getPublishedEventFullInfo for {} started", eventId);
        Event event = getEventById(eventId);
        if (EventState.PUBLISHED.equals(event.getState())) {
            log.info("getPublishedEventFullInfo for {} finished", eventId);
            return mapper.map(event, EventFullDto.class);
        } else {
            throw new EventNotFoundException(String
                    .format("Event %d not found", eventId));
        }
    }
}
