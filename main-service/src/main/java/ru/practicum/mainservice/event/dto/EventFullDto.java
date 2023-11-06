package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.user.dto.UserShortDto;
import ru.practicum.mainservice.util.State;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private int participantLimit;

    private boolean requestModeration;

    private State state;

    private String title;

    private Long views;
}
