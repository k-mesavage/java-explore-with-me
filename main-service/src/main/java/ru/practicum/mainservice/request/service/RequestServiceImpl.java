package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
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

    private final EventRequestRepository eventRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserChecker userChecker;
    private final EventChecker eventChecker;
    private final RequestChecker requestChecker;
    private final RequestMapper mapper;

    @Override
    public EventRequestDto createRequest(Long userId, Long eventId) {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiatorIsNot(eventId, userId);
        requestChecker.requestAlreadyExist(userId, eventId);
        eventChecker.eventPublishedState(eventId);
        EventRequest request = new EventRequest();
        request.setRequester(userRepository.getReferenceById(userId));
        request.setCreated(LocalDateTime.now());
        Event event = eventRepository.getReferenceById(eventId);
        request.setEvent(event);
        try {
            eventChecker.checkEventLimit(eventId);
        } catch (IncorrectFieldException e) {
            request.setStatus(REJECTED);
            eventRequestRepository.save(request);
            throw new IncorrectFieldException("Participants limit for this event has been reached");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request.setStatus(PENDING);
        }
        return mapper.toDto(eventRequestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult patchRequest(EventRequestStatusUpdateRequest updateRequest,
                                                       Long userId,
                                                       Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        eventChecker.eventInitiator(eventId, userId);
        long eventConfirmedRequests = eventRequestRepository.getEventRequestCountByStatus(eventId, State.CONFIRMED);
        List<EventRequest> requests = eventRequestRepository.findByIdIn(updateRequest.getRequestIds());
        if (requests.stream().anyMatch(request -> !request.getStatus().equals(State.PENDING)))
            throw new IncorrectFieldException("Request must have status PENDING");
        if (updateRequest.getStatus().equals(CONFIRMED) && event.getParticipantLimit() > 0
                && event.getParticipantLimit() <= eventConfirmedRequests)
            throw new IncorrectFieldException("The participant limit has been reached");
        for (EventRequest request : requests) {
            if (event.getParticipantLimit() == 0) {
                request.setStatus(CONFIRMED);
            }
            if (updateRequest.getStatus().equals(REJECTED)) {
                request.setStatus(REJECTED);
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            }
            if (event.getConfirmedRequests() <= event.getParticipantLimit()) {
                if (request.getStatus().equals(PENDING)) {
                    request.setStatus(CONFIRMED);
                    eventRequestRepository.save(request);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                }
            } else {
                if (request.getStatus().equals(CONFIRMED)) {
                    throw new IncorrectFieldException("Status is already confirmed");
                }
                request.setStatus(REJECTED);
                eventRequestRepository.save(request);
            }
        }
        eventRepository.save(event);

        EventRequestStatusUpdateResult.EventRequestStatusUpdateResultBuilder builder = EventRequestStatusUpdateResult.builder();
        for (EventRequest request : requests) {
            if (State.CONFIRMED.equals(request.getStatus())) {
                builder.confirmedRequests(List.of(mapper.toDto(eventRequestRepository.getReferenceById(request.getId()))));
            } else {
                builder.rejectedRequests(List.of(mapper.toDto(eventRequestRepository.getReferenceById(request.getId()))));
            }
        }
        return builder.build();
    }

    @Override
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        userChecker.checkUserExists(userId);
        requestChecker.requestExists(requestId);
        requestChecker.requester(userId, requestId);
        requestChecker.canceled(requestId);
        EventRequest request = eventRequestRepository.getReferenceById(requestId);
        request.setStatus(State.CANCELED);
        return mapper.toDto(eventRequestRepository.save(request));
    }

    @Override
    public List<EventRequestDto> getRequestsByRequesterId(Long userId) {
        userChecker.checkUserExists(userId);
        return mapper.toDtosList(eventRequestRepository.findAllByRequesterId(userId));
    }

    @Override
    public EventRequestDto confirmRequestByInitiator(Long userId, Long eventId, Long reqId) {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestExists(reqId);
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.reConfirmed(reqId);
        EventRequest request = eventRequestRepository.getReferenceById(reqId);
        request.setStatus(CONFIRMED);
        Event event = eventRepository.getReferenceById(eventId);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        cancelOtherRequests(eventId);
        return mapper.toDto(eventRequestRepository.save(request));
    }

    @Override
    public EventRequestDto rejectRequestByInitiator(Long userId, Long eventId, Long reqId) {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        requestChecker.requestExists(reqId);
        eventChecker.eventInitiator(eventId, userId);
        requestChecker.correctEventRequest(eventId, reqId);
        requestChecker.pending(reqId);
        EventRequest request = eventRequestRepository.getReferenceById(reqId);
        request.setStatus(REJECTED);
        return mapper.toDto(eventRequestRepository.save(request));
    }

    @Override
    public List<EventRequestDto> getRequestsByInitiator(Long userId, Long eventId) {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        return mapper.toDtosList(eventRequestRepository.findAllByInitiator(userId, eventId));
    }

    private void cancelOtherRequests(Long eventId) {
        final Event event = eventRepository.getReferenceById(eventId);
        int spareParticipantSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (spareParticipantSlots == 0) {
            List<EventRequest> notConfirmedRequests = eventRequestRepository
                    .findAllNotConfirmedRequestsByEventId(eventId);
            for (EventRequest request : notConfirmedRequests) {
                request.setStatus(REJECTED);
                eventRequestRepository.save(request);
            }
        }
    }
}
