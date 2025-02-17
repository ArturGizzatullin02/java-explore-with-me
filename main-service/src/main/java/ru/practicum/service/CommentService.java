package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto);

    CommentDto patchCommentTextOfCurrentUser(long userId, long commentId, NewCommentDto newCommentDto);

    CommentDto patchCommentText(long commentId, NewCommentDto newCommentDto);

    void deleteCommentOfCurrentUser(long userId, long commentId);

    void deleteComment(long commentId);

    CommentDto getCommentById(long commentId);

    List<CommentDto> getCommentsByEventId(long eventId, int from, int size);
}
