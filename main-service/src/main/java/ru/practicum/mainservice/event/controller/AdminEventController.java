package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequestDto;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Valid
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/events")
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventRequestDto eventRequest)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException {
        log.info("Admin update event {}", eventId);
        return eventService.updateEventByAdmin(eventId, eventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) throws WrongConditionException, IncorrectObjectException, ObjectNotFoundException {
        log.info("Admin publish event {}", eventId);
        return eventService.publishEventByAdmin(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) throws WrongConditionException, IncorrectObjectException, ObjectNotFoundException {
        log.info("Admin reject event {}", eventId);
        return eventService.rejectEventByAdmin(eventId);
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Admin get events");
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
