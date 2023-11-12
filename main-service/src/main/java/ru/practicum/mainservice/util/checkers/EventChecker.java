package ru.practicum.mainservice.util.checkers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.dto.UpdateEventRequestDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.enums.State;
import ru.practicum.mainservice.util.enums.StateAction;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventChecker {

    private final EventRepository eventRepository;

    public void isEventDateBeforeTwoHours(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongConditionException("Incorrect time of event");
        }
    }

    public void eventExist(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException("There is no event");
        }
    }

    public void eventNotPublished(Event event) {
    public void eventPublished(Event event) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new IncorrectFieldException("Event already published");
        }
    }

    public void statusForAdminUpdate(Event event, UpdateEventRequestDto requestDto) {
        StateAction stateAction = requestDto.getStateAction();
        if (stateAction != null) {
            if (event.getState().equals(State.PUBLISHED) && stateAction.equals(StateAction.PUBLISH_EVENT)) {
                throw new IncorrectFieldException("Event already published");
            }
            if (event.getState().equals(State.CANCELED) && stateAction.equals(StateAction.PUBLISH_EVENT)) {
                throw new IncorrectFieldException("Event publish canceled");
            }
            if (event.getState().equals(State.PUBLISHED) && stateAction.equals(StateAction.REJECT_EVENT)) {
                throw new IncorrectFieldException("Event already published");
            }
        }
    }

    public void eventInitiatorIsNot(Long eventId, Long userId) {
        if (Objects.equals(eventRepository.getReferenceById(eventId).getInitiator().getId(), userId)) {
            throw new IncorrectFieldException("User id = " + userId + " is initiator of event id = " + eventId);
        }
    }

    public void eventInitiator(Long eventId, Long userId) {
        if (!Objects.equals(eventRepository.getReferenceById(eventId).getInitiator().getId(), userId)) {
            throw new IncorrectFieldException("User id = " + userId + " is not initiator of event id = " + eventId);
        }
    }

    public void eventPublished(Long eventId) {
    public void eventPublishedState(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IncorrectFieldException("It is impossible to create a request to not PUBLISHED event");
        }
    }

    public void notPublished(State state) {
        if (state.equals(State.PUBLISHED)) {
            throw new IncorrectFieldException("Event in state " + state + " cannot be changed");
        }
    }

    public void checkEventLimit(Long eventId) {
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getParticipantLimit() != 0 && Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            throw new IncorrectFieldException("Participants limit for this event has been reached");
        }
    }
}
