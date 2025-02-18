package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.exception.CommentNotFoundException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.PermissionDeniedException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    public CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto) {
        log.info("Started create comment for event {} by user {} with text {}", eventId, userId, newCommentDto);
        Comment comment = mapper.map(newCommentDto, Comment.class);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(String
                .format("Event with id %s not found", eventId)));
        comment.setEvent(event);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new PermissionDeniedException(String
                    .format("User with id %s not allowed to create comment for doesn't published event with id %s", userId, eventId));
        }

        User author = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String
                .format("User with id %s not found", userId)));
        comment.setAuthor(author);

        event.getComments().add(comment);

        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        eventRepository.save(event);

        CommentDto savedCommentDto = mapper.map(savedComment, CommentDto.class);
        log.info("Finished create comment for event {} by user {} with text {}", eventId, userId, savedCommentDto);
        return savedCommentDto;
    }

    @Override
    public CommentDto editCommentTextOfCurrentUser(long userId, long commentId, NewCommentDto newCommentDto) {
        log.info("Started patch comment {} by user {} with text {}", commentId, userId, newCommentDto);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(String
                .format("Comment with id %s not found", commentId)));
        if (comment.getAuthor().getId() != userId) {
            throw new PermissionDeniedException(String
                    .format("User with id %s not allowed to edit comment with id %s", userId, commentId));
        }
        comment.setText(newCommentDto.getText());
        commentRepository.save(comment);
        CommentDto savedCommentDto = mapper.map(comment, CommentDto.class);
        log.info("Finished patch comment {} by user {} with text {} with result {}",
                commentId, userId, newCommentDto, savedCommentDto);
        return savedCommentDto;
    }

    @Override
    public CommentDto editCommentText(long commentId, NewCommentDto newCommentDto) {
        log.info("Started patch comment {} with text {}", commentId, newCommentDto);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(String
                .format("Comment with id %s not found", commentId)));
        comment.setText(newCommentDto.getText());
        commentRepository.save(comment);
        CommentDto savedCommentDto = mapper.map(comment, CommentDto.class);
        log.info("Finished patch comment {} with text {} with result {}", commentId, newCommentDto, savedCommentDto);
        return savedCommentDto;
    }

    @Override
    public void deleteCommentOfCurrentUser(long userId, long commentId) {
        log.info("Started delete comment for {} by user {}", commentId, userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(String
                .format("Comment with id %s not found", commentId)));
        if (comment.getAuthor().getId() != userId) {
            throw new PermissionDeniedException(String
                    .format("User with id %s not allowed to delete comment with id %s", userId, commentId));
        }
        commentRepository.deleteById(commentId);
        log.info("Finished delete comment for {} by user {}", commentId, userId);
    }

    @Override
    public void deleteComment(long commentId) {
        log.info("Started delete comment for {}", commentId);
        commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(String
                .format("Comment with id %s not found", commentId)));
        commentRepository.deleteById(commentId);
        log.info("Finished delete comment for {}", commentId);
    }

    @Override
    public CommentDto getCommentById(long commentId) {
        log.info("Started get comment for {}", commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(String
                .format("Comment with id %s not found", commentId)));
        CommentDto savedCommentDto = mapper.map(comment, CommentDto.class);
        log.info("Finished get comment for {}", savedCommentDto);
        return savedCommentDto;
    }

    @Override
    public List<CommentDto> getCommentsByEventId(long eventId, int from, int size) {
        log.info("Started get comments for event {} from {} size {}", eventId, from, size);
        PageRequest page = PageRequest.of(from / size, size);
        Page<Comment> comments = commentRepository.findByEventId(eventId, page);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> mapper.map(comment, CommentDto.class))
                .toList();
        log.info("Finished get comments for event {} from {} size {} with result {}", eventId, from, size, commentDtos);
        return commentDtos;
    }
}
