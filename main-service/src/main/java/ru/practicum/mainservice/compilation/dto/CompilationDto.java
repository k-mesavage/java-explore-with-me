package ru.practicum.mainservice.compilation.dto;

import lombok.*;
import ru.practicum.mainservice.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Integer id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
