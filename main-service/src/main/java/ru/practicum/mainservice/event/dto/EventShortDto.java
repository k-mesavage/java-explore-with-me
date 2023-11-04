package ru.practicum.mainservice.event.dto;

import lombok.*;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.user.dto.UserShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class EventShortDto {

    private Long id;
    @NotEmpty
    private String annotation;
    @NotNull
    private CategoryDto category;
    @NotEmpty
    private LocalDateTime eventDate;
    @NotEmpty
    private UserShortDto initiator;
    @NotEmpty
    private boolean paid;
    @NotEmpty
    private String title;
    private Long views;
}
