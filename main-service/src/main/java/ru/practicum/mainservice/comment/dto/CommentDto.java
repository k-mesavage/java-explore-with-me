package ru.practicum.mainservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    private Long userId;

    private Long eventId;

    private Long text;

    private LocalDateTime created;
}
