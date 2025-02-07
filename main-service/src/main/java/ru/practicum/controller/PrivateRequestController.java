//package ru.practicum.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import ru.practicum.dto.ParticipationRequestDto;
//
//import java.util.List;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//public class PrivateRequestController {
//
//    private final RequestService requestService;
//
//    @GetMapping("/users/{userId}/requests")
//    public List<ParticipationRequestDto> getRequests(@PathVariable long userId) {
//        log.info("getRequests for {} started", userId);
//        List<ParticipationRequestDto> participationRequestDto = requestService.getRequests(userId);
//        log.info("getRequests for {} finished", participationRequestDto);
//        return participationRequestDto;
//    }
//
//    @PostMapping("/users/{userId}/requests")
//    public ParticipationRequestDto createRequest(@PathVariable long userId, @RequestParam long eventId) {
//        log.info("createRequest for {} {} started", userId, eventId);
//        ParticipationRequestDto participationRequestDto = requestService.saveRequest(userId, eventId);
//        log.info("createRequest for {} {} finished", userId, participationRequestDto);
//        return participationRequestDto;
//    }
//
//    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
//    public ParticipationRequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
//        log.info("cancelRequest for {} {} started", userId, requestId);
//        ParticipationRequestDto participationRequestDto = requestService.cancelRequest(userId, requestId);
//        log.info("cancelRequest for {} {} finished", userId, participationRequestDto);
//        return participationRequestDto;
//    }
//}
