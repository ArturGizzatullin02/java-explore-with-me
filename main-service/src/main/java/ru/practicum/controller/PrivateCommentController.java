package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.service.CommentService;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable long userId, @PathVariable long eventId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Started create comment for event {} by user {}. Comment: {}", eventId, userId, newCommentDto);
        CommentDto commentDto = commentService.createComment(userId, eventId, newCommentDto);
        log.info("Finished create comment for event {} by user {}. Comment: {}", eventId, userId, commentDto);
        return commentDto;
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentOfCurrentUser(@PathVariable long userId, @PathVariable long commentId) {
        log.info("Started delete comment {} by user {}", commentId, userId);
        commentService.deleteCommentOfCurrentUser(userId, commentId);
        log.info("Finished delete comment {} by user {}", commentId, userId);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto editCommentTextOfCurrentUser(@PathVariable long userId, @PathVariable long commentId, @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Started update comment {} by user {}. New comment: {}", commentId, userId, newCommentDto);
        CommentDto commentDto = commentService.editCommentTextOfCurrentUser(userId, commentId, newCommentDto);
        log.info("Finished update comment {} by user {}. New comment: {}", commentId, userId, commentDto);
        return commentDto;
    }
}
