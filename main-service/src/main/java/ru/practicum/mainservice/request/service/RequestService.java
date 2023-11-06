package ru.practicum.mainservice.request.service;

import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;
    EventRequestStatusUpdateResult changeRequestStatus(EventRequestStatusUpdateRequest request, Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException;
    ParticipationRequestDto cancelRequest(Long userId, Long requestId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;
    List<ParticipationRequestDto> getRequestsByRequesterId(Long userId) throws IncorrectObjectException;
    ParticipationRequestDto confirmRequestByInitiator(Long initiatorId, Long eventId, Long reqId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;
    ParticipationRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;
    List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException;
}
