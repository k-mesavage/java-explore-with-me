package ru.practicum.mainservice.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Valid
@Builder
@AllArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
