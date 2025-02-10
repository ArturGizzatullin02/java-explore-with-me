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
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;
import ru.practicum.model.QEvent;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public List<EventFullDto> getEventsByParams(GetEventParametersAdminRequest parameters) {
        log.info("getEventsByParams for {} started", parameters);
        PageRequest page = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());

        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder(); // TODO надо перегружать метод по параметрам для public,admin,private

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
        List<Long> eventIds = events.stream().map(Event::getId).toList();

        List<Event> eventEntities = eventRepository.findAllWithCategoryAndLocationByIds(eventIds);
        List<EventFullDto> eventFullDtos = eventEntities.stream()
                .map(eventEntity -> mapper.map(eventEntity, EventFullDto.class))
                .toList();
        log.info("getEventsByParams for {} finished", parameters); /* этот лог выводится,
        затем
        2025-02-10T21:05:17.797769949Z Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ImmutableCollections$ListN[0]->ru.practicum.dto.EventFullDto["location"]->ru.practicum.model.Location$HibernateProxy$NsbJmRJa["hibernateLazyInitializer"])
        */
        return eventFullDtos;
    }

    @Override
    public EventFullDto patchEvent(long eventId, UpdateEventAdminRequest event) {
        log.info("patchEvent for {} started for {}", eventId, event);
        Event eventFromRepository = eventRepository.findWithCategoryAndLocationById(eventId) // TODO здесь тоже попробовал этот метод, надо спросить где это критично, а где нет
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

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
            eventFromRepository.setLocation(event.getLocation());
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
        log.info("patchEvent for {} finished for {}", eventId, result);
        return result;
    }

    @Override
    public List<EventShortDto> getEventsByCurrentUser(long userId, int from, int size) {
        return List.of();
    }

    @Override
    public EventFullDto saveEvent(long userId, NewEventDto eventDto) {
        log.info("saveEvent for {} for user {} started", eventDto, userId);
        Event event = mapper.map(eventDto, Event.class);
        event.setCreatedOn(LocalDateTime.now());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User %d not found", userId)));
        event.setInitiator(user);
        event.setState(EventState.WAITING);

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(String
                        .format("Category %d not found", eventDto.getCategory())));

        event.setCategory(category);

        Location location = eventDto.getLocation();

        if (location.getId() == null) {
            location = createLocation(location);
        }
        event.setLocation(location);

        log.info("event for save prepared {}", event);
        Event savedEvent = eventRepository.save(event);
        EventFullDto result = mapper.map(savedEvent, EventFullDto.class);
        log.info("saveEvent for {} for user {} finished", eventDto, userId);
        return result;
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
            EventRequestStatusUpdateRequest statusUpdateRequest) {


        return null;
    }

    private Location createLocation(Location location) {
        log.info("createLocation for {} started", location);
        Location savedLocation = locationRepository.save(location);
        log.info("createLocation for {} finished", location);
        return savedLocation;
    }
}
