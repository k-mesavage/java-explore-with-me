package ru.practicum.mainservice.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long userId;

    private Long eventId;

    private String text;

    private LocalDateTime created;
}
