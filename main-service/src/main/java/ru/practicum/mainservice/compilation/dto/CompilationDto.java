package ru.practicum.mainservice.compilation.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;
import ru.practicum.mainservice.event.dto.EventShortDto;

import java.util.List;

@Builder
public class CompilationDto {
    private List<EventShortDto> events;
    @NonNull
    private long id;
    @NonNull
    private boolean pinned;
    @NonNull
    private String title;
}
