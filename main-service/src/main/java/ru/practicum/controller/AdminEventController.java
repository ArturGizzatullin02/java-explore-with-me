//package ru.practicum.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import ru.practicum.dto.EventFullDto;
//import ru.practicum.dto.GetEventParametersAdminRequest;
//import ru.practicum.model.EventState;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//public class AdminEventController {
//
//    private final EventService eventService;
//
//    @GetMapping("/admin/events")
//    public List<EventFullDto> getEventsByParams(@RequestParam List<Integer> users,
//                                                @RequestParam List<EventState> states,
//                                                @RequestParam List<Integer> categories,
//                                                @RequestParam LocalDateTime rangeStart,
//                                                @RequestParam LocalDateTime rangeEnd,
//                                                @RequestParam(defaultValue = "0") int from,
//                                                @RequestParam(defaultValue = "10") int size) {
//
//        GetEventParametersAdminRequest parameters = GetEventParametersAdminRequest.builder()
//                .users(users)
//                .states(states)
//                .categories(categories)
//                .rangeStart(rangeStart)
//                .rangeEnd(rangeEnd)
//                .from(from)
//                .size(size)
//                .build();
//
//        log.info("getEvents for {} started", parameters);
//        List<EventFullDto> eventFullDtos = eventService.getEventsByParams(parameters);
//        log.info("getEvents for {} finished", parameters);
//        return eventFullDtos;
//    }
//
//    @PatchMapping("/admin/events/{eventId}")
//    public EventFullDto patchEvent(@PathVariable long eventId) {
//        log.info("patchEvent for {} started", eventId);
//        EventFullDto eventFullDto = eventService.patchEvent(eventId);
//        log.info("patchEvent for {} finished", eventFullDto);
//        return eventFullDto;
//    }
//}
