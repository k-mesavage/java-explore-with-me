package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.EventRequestDto;

import java.util.List;

public interface RequestService {

    EventRequestDto createRequest(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;
    EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest request, Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException;
    EventRequestDto cancelRequest(Long userId, Long requestId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;
    List<EventRequestDto> getRequestsByRequesterId(Long userId) throws IncorrectObjectException;
    EventRequestDto confirmRequestByInitiator(Long initiatorId, Long eventId, Long reqId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;
    EventRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;
    List<EventRequestDto> getRequestsByInitiator(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException;
}
