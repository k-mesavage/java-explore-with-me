package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventUserRequestDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody NewEventDto newEventDto)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("User add event {}", newEventDto);
        return eventService.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody UpdateEventUserRequestDto requestDto)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("User update event {}", requestDto);
        return eventService.updateEvent(requestDto, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("User cancel event {}", eventId);
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                            @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("User get event {} by initiator {}", userId, eventId);
        return eventService.getEventByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @PathVariable Long reqId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("User confirm request {}", reqId);
        return requestService.confirmRequestByInitiator(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @PathVariable Long reqId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException {
        log.info("User reject request {}", reqId);
        return requestService.rejectRequestByInitiator(userId, eventId, reqId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("User {} get requests", userId);
        return requestService.getRequestsByInitiator(userId, eventId);
    }
}
