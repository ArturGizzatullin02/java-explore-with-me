package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/events/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable long commentId) {
        log.info("Started getCommentById for comment {}", commentId);
        CommentDto comment = commentService.getCommentById(commentId);
        log.info("Finished getCommentById for comment {}", commentId);
        return comment;
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getCommentsByEventId(@PathVariable long eventId,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Started getCommentsByEventId for event {}", eventId);
        List<CommentDto> comments = commentService.getCommentsByEventId(eventId, from, size);
        log.info("Finished getCommentsByEventId for event {}", eventId);
        return comments;
    }
}
