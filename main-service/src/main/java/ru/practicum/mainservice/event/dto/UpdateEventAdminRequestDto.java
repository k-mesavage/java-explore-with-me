package ru.practicum.mainservice.event.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.util.State;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class UpdateEventAdminRequestDto {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private State stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
