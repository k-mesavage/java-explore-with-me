package ru.practicum.mainservice.request.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.request.dto.EventRequestDto;
import ru.practicum.mainservice.request.model.EventRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestMapper {

    public EventRequestDto toDto(EventRequest request) {
        return new EventRequestDto(
                request.getId(),
                request.getRequester().getId(),
                request.getEvent().getId(),
                request.getCreated(),
                request.getStatus().toString());
    }

    public List<EventRequestDto> toDtosList(Iterable<EventRequest> requests) {
        List<EventRequestDto> eventRequestDtos = new ArrayList<>();
        for (EventRequest request : requests) {
            eventRequestDtos.add(toDto(request));
        }
        return eventRequestDtos;
    }
}
