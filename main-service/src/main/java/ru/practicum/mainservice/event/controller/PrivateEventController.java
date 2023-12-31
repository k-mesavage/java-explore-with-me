package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequestDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.EventRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
                                 @Valid @RequestBody NewEventDto newEventDto) {
        log.info("User add event {}", newEventDto);
        return eventService.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventRequestDto requestDto) {
        log.info("User update event {}", requestDto);
        return eventService.updateEvent(requestDto, userId, eventId);
    }

    @DeleteMapping("/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("User cancel event {}", eventId);
        return eventService.cancelEvent(userId, eventId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("User get event {} by initiator {}", userId, eventId);
        return eventService.getEventByInitiator(userId, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEventsByInitiator(@PathVariable Long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size,
                                                   HttpServletRequest request) {
        return eventService.getEventsByInitiator(userId, from, size, request);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public EventRequestDto confirmRequest(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @PathVariable Long reqId) {
        log.info("User confirm request {}", reqId);
        return requestService.confirmRequestByInitiator(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public EventRequestDto rejectRequest(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @PathVariable Long reqId) {
        log.info("User reject request {}", reqId);
        return requestService.rejectRequestByInitiator(userId, eventId, reqId);
    }

    @GetMapping("/{eventId}/requests")
    public List<EventRequestDto> getRequests(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("User {} get requests", userId);
        return requestService.getRequestsByInitiator(userId, eventId);
    }
}
