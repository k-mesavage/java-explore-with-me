package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.ParticipationMapper;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.repository.ParticipationRepository;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserChecker userChecker;
    private final EventChecker eventChecker;
    private final RequestChecker requestChecker;
    private final StatusChecker statusChecker;
    private final ParticipationMapper mapper;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestAlreadyExist(userId, eventId);
        eventChecker.eventInitiatorIsNot(eventId, userId);
        eventChecker.eventPublishedState(eventId);
        eventChecker.checkEventLimit(eventId);
        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(userRepository.getById(userId));
        request.setEvent(eventRepository.getById(eventId));
        request.setCreated(LocalDateTime.now());
        request.setStatus(State.CONFIRMED);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(EventRequestStatusUpdateRequest request, Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        final State newStatus = request.getStatus();
        userChecker.checkUserExists(userId);
        final Event event = eventRepository.getReferenceById(eventId);
        eventChecker.eventInitiator(eventId, userId);
        final List<Long> requestIds = request.getRequestIds();
        final int participantLimit = event.getParticipantLimit();
        int currentConfirmed = event.getConfirmedRequests();
        final Boolean isRequestModeration = event.getRequestModeration();
        if (participantLimit > 0 && participantLimit == currentConfirmed) {
            throw new IncorrectFieldException("The limit on confirmations");
        }
        final List<ParticipationRequest> requestList = participationRepository.findAllByIdInAndStatus(requestIds, State.PENDING);
        if (requestList.size() != requestIds.size()) {
            throw new IncorrectFieldException("Some request is not a PENDING");
        }
        final List<ParticipationRequestDto> confirmedDto = new ArrayList<>();
        final List<ParticipationRequestDto> rejectedList = new ArrayList<>();
        statusChecker.checkStatus(newStatus);
        if (State.REJECTED.equals(newStatus)) {
            requestList.forEach(r -> r.setStatus(State.REJECTED));
            rejectedList.addAll(requestList.stream()
                    .map(mapper::toDto).collect(Collectors.toList()));
        }
        if (State.CONFIRMED.equals(newStatus)) {
            if ((participantLimit == 0) || FALSE.equals(isRequestModeration)) {
                requestList.forEach(r -> r.setStatus(State.CONFIRMED));
                final List<ParticipationRequestDto> confirmedList = requestList.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
                participationRepository.saveAll(requestList);
                return new EventRequestStatusUpdateResult(confirmedList, Collections.emptyList());
            }
            for (ParticipationRequest r : requestList) {
                if (currentConfirmed < participantLimit) {
                    r.setStatus(State.CONFIRMED);
                    currentConfirmed++;
                } else {
                    r.setStatus(State.REJECTED);
                }
            }
            event.setConfirmedRequests(currentConfirmed);
            eventRepository.save(event);
            confirmedDto.addAll(requestList.stream()
                    .filter(RequestServiceImpl::isConfirmedRequest)
                    .map(mapper::toDto)
                    .collect(Collectors.toList()));
            rejectedList.addAll(requestList.stream()
                    .filter(RequestServiceImpl::isRejectedRequest)
                    .map(mapper::toDto)
                    .collect(Collectors.toList()));
        }
        participationRepository.saveAll(requestList);
        return new EventRequestStatusUpdateResult(confirmedDto, rejectedList);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userChecker.checkUserExists(userId);
        requestChecker.requestExists(requestId);
        requestChecker.requester(userId, requestId);
        requestChecker.canceled(requestId);
        ParticipationRequest request = participationRepository.getById(requestId);
        request.setStatus(State.CANCELED);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByRequesterId(Long userId) throws IncorrectObjectException {
        userChecker.checkUserExists(userId);
        return mapper.toDtosList(participationRepository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto confirmRequestByInitiator(Long userId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestExists(reqId);
        eventChecker.eventInitiator(eventId, userId);
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.reConfirmed(reqId);
        ParticipationRequest request = participationRepository.getById(reqId);
        request.setStatus(State.CONFIRMED);
        Event event = eventRepository.getById(eventId);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        cancelOtherRequests(eventId);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestExists(reqId);
        eventChecker.eventInitiator(eventId, userId);
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.pending(reqId);
        ParticipationRequest request = participationRepository.getById(reqId);
        request.setStatus(State.REJECTED);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        return mapper.toDtosList(participationRepository.findAllByInitiator(userId, eventId));
    }

    private void cancelOtherRequests(Long eventId) {
        final Event event = eventRepository.getById(eventId);
        int spareParticipantSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (spareParticipantSlots == 0) {
            List<ParticipationRequest> notConfirmedRequests = participationRepository
                    .findAllNotConfirmedRequestsByEventId(eventId);
            for (ParticipationRequest request : notConfirmedRequests) {
                request.setStatus(State.REJECTED);
                participationRepository.save(request);
            }
        }
    }

    private static boolean isConfirmedRequest(ParticipationRequest r) {
        return State.CONFIRMED.equals(r.getStatus());
    }

    private static boolean isRejectedRequest(ParticipationRequest r) {
        return State.REJECTED.equals(r.getStatus());
    }
}
