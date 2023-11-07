package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequestDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.SQLException;
import java.util.List;

@Valid
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto newEventDto)
            throws IncorrectObjectException, SQLException, WrongConditionException, ObjectNotFoundException {
        log.info("User add event {}", newEventDto);
        return eventService.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventRequestDto requestDto)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("User update event {}", requestDto);
        return eventService.updateEvent(requestDto, userId, eventId);
    }

    @DeleteMapping("/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("User cancel event {}", eventId);
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                            @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("User get event {} by initiator {}", userId, eventId);
        return eventService.getEventByInitiator(userId, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEventsByInitiator(@PathVariable Long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size,
                                                   HttpServletRequest request) throws IncorrectObjectException {
        return eventService.getEventsByInitiator(userId, from, size, request);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long reqId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("User confirm request {}", reqId);
        return requestService.confirmRequestByInitiator(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @PathVariable Long reqId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("User reject request {}", reqId);
        return requestService.rejectRequestByInitiator(userId, eventId, reqId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("User {} get requests", userId);
        return requestService.getRequestsByInitiator(userId, eventId);
    }
}
