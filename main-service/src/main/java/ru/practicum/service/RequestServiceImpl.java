package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.model.ParticipationRequestStatus;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
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
        log.info("saveRequestOfCurrentUser for {} started", userId);
        Request request = Request.builder()
                .requester(userId)
                .event(eventId)
                .created(LocalDateTime.now())
                .status(ParticipationRequestStatus.PENDING)
                .build();
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
        request.setStatus(ParticipationRequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        ParticipationRequestDto result = mapper.map(savedRequest, ParticipationRequestDto.class);
        log.info("cancelRequestOfCurrentUser for {} {} finished", userId, requestId);
        return result;
    }
}
