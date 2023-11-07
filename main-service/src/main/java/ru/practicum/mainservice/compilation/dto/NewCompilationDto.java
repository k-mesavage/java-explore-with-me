package ru.practicum.mainservice.compilation.dto;

import lombok.*;
import ru.practicum.mainservice.util.constraints.Create;
import ru.practicum.mainservice.util.constraints.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    private String title;
}
