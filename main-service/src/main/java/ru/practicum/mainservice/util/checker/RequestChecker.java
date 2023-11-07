package ru.practicum.mainservice.util.checker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.repository.ParticipationRepository;
import ru.practicum.mainservice.util.State;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestChecker {

    private final ParticipationRepository repository;

    public void pending(Long reqId) throws WrongConditionException {
        final ParticipationRequest request = repository.getReferenceById(reqId);
        if (!request.getStatus().equals(State.PENDING)) {
            throw new WrongConditionException("Only request in status PENDING can be rejected");
        }
    }

    public void reConfirmed(Long reqId) throws WrongConditionException {
        final ParticipationRequest request = repository.getReferenceById(reqId);
        if (request.getStatus().equals(State.CONFIRMED)) {
            throw new WrongConditionException("Request in already in status CONFIRMED");
        }
    }

    public void correctEventRequest(Long eventId, Long reqId) throws IncorrectFieldException {
        final ParticipationRequest request = repository.getReferenceById(reqId);
        if (!Objects.equals(request.getEvent().getId(), eventId)) {
            throw new IncorrectFieldException("Incorrect event request");
        }
    }

    public void requestAlreadyExist(Long userId, Long eventId) throws IncorrectFieldException {
        if (repository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new IncorrectFieldException("Request already exist");
        }
    }

    public void requester(Long userId, Long requestId) throws IncorrectFieldException {
        ParticipationRequest request = repository.getById(requestId);
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new IncorrectFieldException("Requester exception");
        }
    }

    public void canceled(Long requestId) throws WrongConditionException {
        ParticipationRequest request = repository.getById(requestId);
        if (request.getStatus().equals(State.CANCELED)) {
            throw new WrongConditionException("Request in already in status CANCELED");
        }
    }

    public void requestExists(Long requestId) throws IncorrectObjectException {
        if (!repository.findAll().isEmpty()) {
            List<Long> ids = repository.findAll()
                    .stream()
                    .map(ParticipationRequest::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(requestId)) {
                throw new IncorrectObjectException("There is no request with id = " + requestId);
            }
        } else {
            throw new IncorrectObjectException("There is no request with id = " + requestId);
        }
    }
}
