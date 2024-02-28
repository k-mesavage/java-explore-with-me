package ru.practicum.mainservice.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
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
        final CommentDto newComment = new CommentDto(userId, eventId, text, LocalDateTime.now());
        eventChecker.eventInitiatorIsNot(eventId, userId);
        commentRepository.save(commentMapper.fromDto(newComment));
        return commentMapper.toDto(newComment);
    }
}
