package ru.practicum.mainservice.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UpdateCompilationRequest {
    private List<EventShortDto> events;
    private boolean pinned;
    private String title;
}
