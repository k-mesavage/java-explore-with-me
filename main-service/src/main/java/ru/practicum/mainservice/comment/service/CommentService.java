package ru.practicum.mainservice.comment.service;

import ru.practicum.mainservice.comment.dto.CommentDto;

import java.time.LocalDateTime;

public interface CommentService {

    CommentDto addComment(String text, Long eventId, Long userId, LocalDateTime created);

}
