package ru.practicum.mainservice.request.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getRequester().getId(),
                request.getEvent().getId(),
                request.getCreated(),
                request.getStatus().toString());
    }

    public List<ParticipationRequestDto> toDtosList(Iterable<ParticipationRequest> requests) {
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            participationRequestDtos.add(toDto(request));
        }
        return participationRequestDtos;
    }
}
