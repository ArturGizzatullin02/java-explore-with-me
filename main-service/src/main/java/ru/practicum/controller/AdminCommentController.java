package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/admin/events/comments/{commentId}")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        log.info("Started delete comment {}", commentId);
        commentService.deleteComment(commentId);
        log.info("Finished delete comment {}", commentId);
    }

    @PatchMapping
    public CommentDto editCommentText(@PathVariable long commentId, @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Started update comment {}. New comment: {}", commentId, newCommentDto);
        CommentDto commentDto = commentService.editCommentText(commentId, newCommentDto);
        log.info("Finished update comment {}. New comment: {}", commentId, commentDto);
        return commentDto;
    }
}
