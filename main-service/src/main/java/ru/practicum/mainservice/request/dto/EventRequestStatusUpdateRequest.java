package ru.practicum.mainservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.mainservice.util.State;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotNull
    @NotEmpty
    private List<Long> requestIds;
    @NotNull
    private State status;
}
