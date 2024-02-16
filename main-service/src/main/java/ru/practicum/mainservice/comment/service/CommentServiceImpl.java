package ru.practicum.mainservice.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.util.checkers.EventChecker;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final EventChecker eventChecker;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    @Override
    public CommentDto addComment(String text, Long eventId, Long userId, LocalDateTime created) {
        Comment newComment = new Comment(text, eventId, userId, created);
        eventChecker.eventInitiatorIsNot(eventId, userId);
        commentRepository.save(newComment);
        return commentMapper.toDto(newComment);
    }
}
