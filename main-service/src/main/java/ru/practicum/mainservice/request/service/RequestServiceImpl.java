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
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.repository.ParticipationRepository;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.mainservice.util.State.*;

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
    private final RequestMapper mapper;
    private final ParticipationRepository repository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId)
            throws IncorrectObjectException, WrongConditionException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestAlreadyExist(userId, eventId);
        eventChecker.eventPublishedState(eventId);
        eventChecker.checkEventLimit(eventId);
        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(userRepository.getById(userId));
        request.setEvent(eventRepository.getById(eventId));
        request.setCreated(LocalDateTime.now());
        request.setStatus(CONFIRMED);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest updateRequest, Long userId, Long eventId)
            throws IncorrectFieldException, ObjectNotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new IncorrectFieldException("User is no initiator this event");
        }
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new IncorrectFieldException("Confirmed requests full");
        }
        List<ParticipationRequest> eventRequestList = repository.findByIdIn((updateRequest.getRequestIds())); // получили список запросов
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        if (updateRequest.getStatus().equals(REJECTED)) {
            for (ParticipationRequest eventRequest : eventRequestList) {
                eventRequest.setStatus(REJECTED);
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                rejectedRequests.add(eventRequest);
            }
        }
        if (event.getParticipantLimit() == 0) {
            confirmedRequests.addAll(eventRequestList);
            updateResult.setConfirmedRequests(mapper.toDtosList(confirmedRequests));
            return updateResult;
        }
        for (ParticipationRequest eventRequest : eventRequestList) {
            if (event.getConfirmedRequests() <= event.getParticipantLimit()) {
                if (eventRequest.getStatus().equals(PENDING)) {

                    eventRequest.setStatus(CONFIRMED);
                    repository.save(eventRequest);
                    confirmedRequests.add(eventRequest);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
            } else {
                if (eventRequest.getStatus().equals(CONFIRMED)) {
                    throw new IncorrectFieldException("Status is already confirmed");
                }
                eventRequest.setStatus(REJECTED);
                repository.save(eventRequest);
                rejectedRequests.add(eventRequest);
            }
        }
        eventRepository.save(event);
        updateResult.setConfirmedRequests(mapper.toDtosList(confirmedRequests));
        updateResult.setRejectedRequests(mapper.toDtosList(rejectedRequests));
        return updateResult;
    }

    private EventRequestStatusUpdateResult createUpdateResult(List<ParticipationRequest> requests) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (ParticipationRequest r: requests) {
            if (r.getStatus().equals(State.CONFIRMED)) {
                confirmedRequests.add(mapper.toDto(r));
            } else {
                rejectedRequests.add(mapper.toDto(r));
            }
        }
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
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
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.reConfirmed(reqId);
        ParticipationRequest request = participationRepository.getById(reqId);
        request.setStatus(CONFIRMED);
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
        request.setStatus(REJECTED);
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
                request.setStatus(REJECTED);
                participationRepository.save(request);
            }
        }
    }

    private static boolean isConfirmedRequest(ParticipationRequest r) {
        return CONFIRMED.equals(r.getStatus());
    }

    private static boolean isRejectedRequest(ParticipationRequest r) {
        return REJECTED.equals(r.getStatus());
    }
}
