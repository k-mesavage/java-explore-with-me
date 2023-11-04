package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.ParticipationMapper;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.repository.ParticipationRepository;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.EventChecker;
import ru.practicum.mainservice.util.RequestChecker;
import ru.practicum.mainservice.util.State;
import ru.practicum.mainservice.util.UserChecker;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserChecker userChecker;
    private final EventChecker eventChecker;
    private final RequestChecker requestChecker;
    private final ParticipationMapper mapper;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
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
        if (eventRepository.getById(eventId).getRequestModeration()) {
            request.setStatus(State.PENDING);
        } else {
            request.setStatus(State.CONFIRMED);
        }
        return mapper.toDto(participationRepository.save(request));
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
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
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
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException {
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
    public List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException {
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
}
