package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.EventRequestDto;

import java.util.List;

public interface RequestService {

    EventRequestDto createRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest request, Long userId, Long eventId);

    EventRequestDto cancelRequest(Long userId, Long requestId);

    List<EventRequestDto> getRequestsByRequesterId(Long userId);

    EventRequestDto confirmRequestByInitiator(Long initiatorId, Long eventId, Long reqId);

    EventRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId);

    List<EventRequestDto> getRequestsByInitiator(Long userId, Long eventId);
}
