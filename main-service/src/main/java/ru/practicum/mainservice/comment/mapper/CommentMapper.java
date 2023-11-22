package ru.practicum.mainservice.comment.mapper;

import ru.practicum.mainservice.comment.dto.CommentDto;
import ru.practicum.mainservice.comment.model.Comment;

public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .userId(comment.getUserId())
                .eventId(comment.getEventId())
                .created(comment.getCreated())
                .text(comment.getText())
                .build();
    }

    public Comment fromDto(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setCreated(commentDto.getCreated());
        comment.setText(commentDto.getText());
        comment.setEventId(comment.getEventId());
        comment.setUserId(commentDto.getUserId());
        return comment;
    }
}
