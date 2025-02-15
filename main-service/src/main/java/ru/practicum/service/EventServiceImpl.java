package ru.practicum.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final RequestRepository requestRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public List<EventFullDto> getEventsByParams(GetEventParametersAdminRequest parameters) {
        log.info("getEventsByParams for {} started", parameters);
        PageRequest page = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());

        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        if (parameters.getUsers() != null && !parameters.getUsers().isEmpty()) {
            predicate.and(event.initiator.id.in(parameters.getUsers()));
        }

        if (parameters.getStates() != null && !parameters.getStates().isEmpty()) {
            predicate.and(event.state.in(parameters.getStates()));
        }

        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(parameters.getCategories()));
        }

        if (parameters.getRangeStart() != null) {
            predicate.and(event.eventDate.after(parameters.getRangeStart()));
        }

        if (parameters.getRangeEnd() != null) {
            predicate.and(event.eventDate.before(parameters.getRangeEnd()));
        }

        Page<Event> events = eventRepository.findAll(predicate, page);
        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventEntity -> mapper.map(eventEntity, EventFullDto.class))
                .toList();
        log.info("getEventsByParams for {} finished", parameters);
        return eventFullDtos;
    }

    public List<EventShortDto> getEventsShortDtoByParams(GetEventParametersUserRequest parameters) {
        log.info("getEventsByParams for {} started", parameters);
        PageRequest page = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());

        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        if (parameters.getText() != null && !parameters.getText().isBlank()) {
            predicate.and(event.annotation.containsIgnoreCase(parameters.getText())
                    .or(event.description.containsIgnoreCase(parameters.getText())));
        }

        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(parameters.getCategories()));
        }

        if (parameters.getPaid() != null) {
            predicate.and(event.paid.eq(parameters.getPaid()));
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

        if (Boolean.TRUE.equals(parameters.getOnlyAvailable())) {
            predicate.and(event.participantLimit.isNull()
                    .or(event.participantLimit.gt(event.confirmedRequests)));
        }

        Page<Event> events = eventRepository.findAll(predicate, page);

        if (parameters.getSort() == null) {
            List<EventShortDto> eventShortDtos = events.stream()
                    .map(eventEntity -> mapper.map(eventEntity, EventShortDto.class))
                    .toList();
            log.info("getEventsByParams for {} finished", parameters);
            return eventShortDtos;
        } else {
            List<Event> sortedEvents;
            switch (parameters.getSort()) {
                case VIEWS:
                    sortedEvents = events.stream()
                            .sorted(Comparator.comparingInt(Event::getViews).reversed())
                            .toList();
                    break;
                case EVENT_DATE:
                default:
                    sortedEvents = events.stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .toList();
                    break;
            }

            List<EventShortDto> eventShortDtos = sortedEvents.stream()
                    .map(eventEntity -> mapper.map(eventEntity, EventShortDto.class))
                    .toList();

            log.info("getEventsByParams for {} finished", parameters);
            return eventShortDtos;
        }
    }


    @Override
    public EventFullDto patchEvent(long eventId, UpdateEventAdminRequest event) {
        log.info("patchEvent for {} started for {}", eventId, event);
        Event eventFromRepository = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (event.getAnnotation() != null) {
            eventFromRepository.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            eventFromRepository.setCategory(categoryRepository.findById(event.getCategory())
                    .orElseThrow(() -> new EventNotFoundException("Category not found")));
        }
        if (event.getDescription() != null) {
            eventFromRepository.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            eventFromRepository.setEventDate(event.getEventDate());
        }
        if (event.getLocation() != null) {
            if (event.getLocation().getId() == null) {
                Location savedLocation = createLocation(event.getLocation());
                eventFromRepository.setLocation(savedLocation);
            } else {
                Location location = locationRepository.findById(event.getLocation().getId())
                        .orElseThrow(() -> new LocationNotFoundException("Location not found"));
                eventFromRepository.setLocation(location);
            }
        }
        if (event.getPaid() != null) {
            eventFromRepository.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            eventFromRepository.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            eventFromRepository.setRequestModeration(event.getRequestModeration());
        }
        if (event.getStateAction() != null) {
            if (eventFromRepository.getState().equals(EventState.CANCELED)) {
                throw new EventAlreadyCanceledException(String.
                        format("Event %d already canceled", eventId));
            } else if ((eventFromRepository.getState().equals(EventState.PUBLISHED))) {
                throw new EventAlreadyCanceledException(String.
                        format("Event %d already published", eventId));
            } else {
                eventFromRepository.setState(event.getStateAction().toEventState());
            }
        }
        if (event.getTitle() != null) {
            eventFromRepository.setTitle(event.getTitle());
        }
        eventFromRepository.setPublishedOn(LocalDateTime.now());
        Event savedEvent = eventRepository.save(eventFromRepository);
        EventFullDto result = mapper.map(savedEvent, EventFullDto.class);
        log.info("patchEvent for {} finished for {}", eventId, result);
        return result;
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new PermissionDeniedException(String.format("User %d not allowed to get full info of event %d", userId, eventId));
        }
        return mapper.map(event, EventFullDto.class);
    }

    @Override
    public EventFullDto patchEvent(long userId, long eventId, UpdateEventUserRequest event) {
        log.info("patchEvent for {} started for {}", eventId, event);
        Event eventFromRepository = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (eventFromRepository.getState().equals(EventState.PUBLISHED)) {
            throw new EventAlreadyPublishedException(String
                    .format("Event %d already published", eventId));
        }
        if (event.getAnnotation() != null) {
            eventFromRepository.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            eventFromRepository.setCategory(categoryRepository.findById(event.getCategory().getId())
                    .orElseThrow(() -> new EventNotFoundException("Category not found")));
        }
        if (event.getDescription() != null) {
            eventFromRepository.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            eventFromRepository.setEventDate(event.getEventDate());
        }
        if (event.getLocation() != null) {
            if (event.getLocation().getId() == null) {
                Location savedLocation = createLocation(event.getLocation());
                eventFromRepository.setLocation(savedLocation);
            } else {
                Location location = locationRepository.findById(event.getLocation().getId())
                        .orElseThrow(() -> new LocationNotFoundException("Location not found"));
                eventFromRepository.setLocation(location);
            }
        }
        if (event.getPaid() != null) {
            eventFromRepository.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            eventFromRepository.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            eventFromRepository.setRequestModeration(event.getRequestModeration());
        }
        if (event.getStateAction() != null) {
            eventFromRepository.setState(event.getStateAction().toEventState());
        }
        if (event.getTitle() != null) {
            eventFromRepository.setTitle(event.getTitle());
        }
        Event savedEvent = eventRepository.save(eventFromRepository);
        EventFullDto result = mapper.map(savedEvent, EventFullDto.class);
        log.info("saveEvent for {} for user {} finished", result, userId);
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByCurrentUserEvents(long userId, long eventId) {
        log.info("getParticipationRequestsByCurrentUserEvents for {} started", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (event.getInitiator().getId() != userId) {
            throw new PermissionDeniedException(String
                    .format("User %d not allowed to get full info of event %d", userId, eventId));
        }

        List<Request> requests = requestRepository.findAllByEvent(eventId);
        List<ParticipationRequestDto> result = requests.stream()
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();
        log.info("getParticipationRequestsByCurrentUserEvents for {} finished", eventId);
        return result;
    }

    @Override
    public EventRequestStatusUpdateResult patchParticipationRequestOfCurrentUserEvents(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest statusUpdateRequest) {

        log.info("patchParticipationRequestOfCurrentUserEvents for event {} user {} requests {} started",
                eventId, userId, statusUpdateRequest.getRequestIds());
        List<Request> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));

        if (event.getInitiator().getId() != userId) {
            throw new PermissionDeniedException(String
                    .format("User %d not allowed to patch event %d", userId, eventId));
        }

        AtomicInteger confirmedRequests = new AtomicInteger(event.getConfirmedRequests());

        if (statusUpdateRequest.getStatus() != null && statusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            requests.forEach(request -> {
                if (!event.getRequestModeration()) {
                    if ((event.getParticipantLimit() < confirmedRequests.get() + requests.size())) {
                        throw new RequestForLimitReachedEventException(String
                                .format("Request for limit reached for event %d", eventId));
                    }
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                } else if (event.getParticipantLimit() == 0) {
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                } else {
                    if (event.getParticipantLimit() <= confirmedRequests.get()) {
                        log.info("eventConfirmedRequests after LIMIT REACHED save request {}", confirmedRequests.get());

                        log.info("LIMIT REACHED WHEN NO MODERATION AND NO PARTICIPANT LIMIT==0");
                        throw new RequestForLimitReachedEventException(String
                                .format("Request for limit reached for event %d", eventId));
                    }
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                    confirmedRequests.getAndIncrement();
                    log.info("eventConfirmedRequests after save request {}", confirmedRequests.get());
                }
            });
        } else if (statusUpdateRequest.getStatus() != null && statusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            requests.forEach(request -> {
                if (request.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
                    throw new RequestAlreadyConfirmedException(String
                            .format("Request %d already confirmed", request.getId()));
                }
                request.setStatus(ParticipationRequestStatus.REJECTED);
            });
        }

        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.get());
        eventRepository.save(event);

        requestRepository.saveAll(requests);

        List<Request> savedRequests = requestRepository.findAllByEvent(eventId);

        List<ParticipationRequestDto> confirmedRequestsDto;
        List<ParticipationRequestDto> rejectedRequestsDto;

        confirmedRequestsDto = savedRequests.stream()
                .filter(request -> request.getStatus().equals(ParticipationRequestStatus.CONFIRMED))
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();

        rejectedRequestsDto = savedRequests.stream()
                .filter(request -> request.getStatus().equals(ParticipationRequestStatus.REJECTED))
                .map(request -> mapper.map(request, ParticipationRequestDto.class))
                .toList();

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequestsDto)
                .rejectedRequests(rejectedRequestsDto)
                .build();

        log.info("event after save: {}", event);
        log.info("patchParticipationRequestOfCurrentUserEvents for event {} user {} requests {} finished",
                eventId, userId, statusUpdateRequest.getRequestIds());
        return result;
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));
        if (event.getState().equals(EventState.PUBLISHED)) {
            log.info("getPublishedEventFullInfo for {} finished", eventId);
            return mapper.map(event, EventFullDto.class);
        } else {
            throw new EventNotFoundException(String
                    .format("Event %d not found", eventId));
        }
    }

    @Override
    public void increaseViews(long eventId, int views) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event %d not found", eventId)));
        event.setViews(views);
        eventRepository.save(event);
    }
}
