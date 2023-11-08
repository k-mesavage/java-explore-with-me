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
import ru.practicum.mainservice.request.dto.EventRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.EventRequest;
import ru.practicum.mainservice.request.repository.EventRequestRepository;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.checkers.EventChecker;
import ru.practicum.mainservice.util.checkers.RequestChecker;
import ru.practicum.mainservice.util.checkers.UserChecker;
import ru.practicum.mainservice.util.enums.State;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.mainservice.util.enums.State.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final EventRequestRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserChecker userChecker;
    private final EventChecker eventChecker;
    private final RequestChecker requestChecker;
    private final RequestMapper mapper;

    @Override
    public EventRequestDto createRequest(Long userId, Long eventId)
            throws IncorrectObjectException, ObjectNotFoundException, IncorrectFieldException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiatorIsNot(eventId, userId);
        requestChecker.requestAlreadyExist(userId, eventId);
        eventChecker.eventPublishedState(eventId);
        EventRequest request = new EventRequest();
        request.setRequester(userRepository.getReferenceById(userId));
        request.setCreated(LocalDateTime.now());
        final Event event = eventRepository.getReferenceById(eventId);
        request.setEvent(event);
        try {
            eventChecker.checkEventLimit(eventId);
        } catch (IncorrectFieldException e) {
            request.setStatus(REJECTED);
            participationRepository.save(request);
            throw new IncorrectFieldException("Participants limit for this event has been reached");
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(CONFIRMED);
        } else {
            request.setStatus(PENDING);
        }
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest updateRequest, Long userId, Long eventId)
            throws IncorrectFieldException {
        final Event event = eventRepository.getReferenceById(eventId);
        eventChecker.eventInitiator(eventId, userId);
        long eventConfirmedRequests = participationRepository.getEventRequestCountByStatus(eventId, State.CONFIRMED);
        List<EventRequest> requests = participationRepository.findByIdIn(updateRequest.getRequestIds());
        if (requests.stream().anyMatch(request -> !request.getStatus().equals(State.PENDING)))
            throw new IncorrectFieldException("Request must have status PENDING");
        if (updateRequest.getStatus().equals(CONFIRMED) && event.getParticipantLimit() > 0
                && event.getParticipantLimit() <= eventConfirmedRequests)
            throw new IncorrectFieldException("The participant limit has been reached");
        for (EventRequest request : requests) {
            if (updateRequest.getStatus().equals(CONFIRMED)) {
                if (event.getParticipantLimit() < 1 || event.getParticipantLimit() >= eventConfirmedRequests++) {
                    request.setStatus(State.CONFIRMED);
                    participationRepository.save(request);
                }
            } else {
                request.setStatus(State.REJECTED);
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                participationRepository.save(request);
            }
        }

        EventRequestStatusUpdateResult.EventRequestStatusUpdateResultBuilder builder = EventRequestStatusUpdateResult.builder();
        for (
                EventRequest request : requests) {
            if (State.CONFIRMED.equals(request.getStatus())) {
                builder.confirmedRequests(List.of(mapper.toDto(participationRepository.getReferenceById(request.getId()))));
            } else {
                builder.rejectedRequests(List.of(mapper.toDto(participationRepository.getReferenceById(request.getId()))));
            }
        }
        return builder.build();
    }

    @Override
    public EventRequestDto cancelRequest(Long userId, Long requestId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
        userChecker.checkUserExists(userId);
        requestChecker.requestExists(requestId);
        requestChecker.requester(userId, requestId);
        requestChecker.canceled(requestId);
        EventRequest request = participationRepository.getReferenceById(requestId);
        request.setStatus(State.CANCELED);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public List<EventRequestDto> getRequestsByRequesterId(Long userId) throws IncorrectObjectException {
        userChecker.checkUserExists(userId);
        return mapper.toDtosList(participationRepository.findAllByRequesterId(userId));
    }

    @Override
    public EventRequestDto confirmRequestByInitiator(Long userId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestExists(reqId);
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.reConfirmed(reqId);
        EventRequest request = participationRepository.getReferenceById(reqId);
        request.setStatus(CONFIRMED);
        Event event = eventRepository.getReferenceById(eventId);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        cancelOtherRequests(eventId);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public EventRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestExists(reqId);
        eventChecker.eventInitiator(eventId, userId);
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.pending(reqId);
        EventRequest request = participationRepository.getReferenceById(reqId);
        request.setStatus(REJECTED);
        return mapper.toDto(participationRepository.save(request));
    }

    @Override
    public List<EventRequestDto> getRequestsByInitiator(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        return mapper.toDtosList(participationRepository.findAllByInitiator(userId, eventId));
    }

    private void cancelOtherRequests(Long eventId) {
        final Event event = eventRepository.getReferenceById(eventId);
        int spareParticipantSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (spareParticipantSlots == 0) {
            List<EventRequest> notConfirmedRequests = participationRepository
                    .findAllNotConfirmedRequestsByEventId(eventId);
            for (EventRequest request : notConfirmedRequests) {
                request.setStatus(REJECTED);
                participationRepository.save(request);
            }
        }
    }
}
