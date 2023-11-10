package ru.practicum.mainservice.util.checkers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.model.EventRequest;
import ru.practicum.mainservice.request.repository.EventRequestRepository;
import ru.practicum.mainservice.util.enums.State;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestChecker {

    private final EventRequestRepository repository;

    public void pending(Long reqId) {
        final EventRequest request = repository.getReferenceById(reqId);
        if (!request.getStatus().equals(State.PENDING)) {
            throw new WrongConditionException("Only request in status PENDING can be rejected");
        }
    }

    public void reConfirmed(Long reqId) {
        final EventRequest request = repository.getReferenceById(reqId);
        if (request.getStatus().equals(State.CONFIRMED)) {
            throw new IncorrectFieldException("Request in already in status CONFIRMED");
        }
    }

    public void correctEventRequest(Long eventId, Long reqId) {
        final EventRequest request = repository.getReferenceById(reqId);
        if (!Objects.equals(request.getEvent().getId(), eventId)) {
            throw new IncorrectFieldException("Incorrect event request");
        }
    }

    public void requestAlreadyExist(Long userId, Long eventId) {
        if (repository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new IncorrectFieldException("Request already exist");
        }
    }

    public void requester(Long userId, Long requestId) {
        EventRequest request = repository.getReferenceById(requestId);
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new IncorrectFieldException("Requester exception");
        }
    }

    public void canceled(Long requestId) {
        EventRequest request = repository.getReferenceById(requestId);
        if (request.getStatus().equals(State.CANCELED)) {
            throw new WrongConditionException("Request in already in status CANCELED");
        }
    }

    public void requestExists(Long requestId) {
        if (!repository.findAll().isEmpty()) {
            List<Long> ids = repository.findAll()
                    .stream()
                    .map(EventRequest::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(requestId)) {
                throw new IncorrectObjectException("There is no request with id = " + requestId);
            }
        } else {
            throw new IncorrectObjectException("There is no request with id = " + requestId);
        }
    }
}
