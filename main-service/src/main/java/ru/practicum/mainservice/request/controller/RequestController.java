package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {

    private final RequestService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/requests")
    public ParticipationRequestDto addRequest(@PathVariable("userId") Long userId,
                                              @RequestParam ("eventId") Long eventId)
            throws WrongConditionException, IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("Add Request");
        return service.createRequest(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@Valid @RequestBody EventRequestStatusUpdateRequest request,
                                                              @PathVariable(value = "userId") long userId,
                                                              @PathVariable(value = "eventId") long eventId)
            throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException {
        log.info("Path request status");
        return service.patchRequest(request, userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(value = "userId") Long userId,
                                                 @PathVariable(value = "requestId") Long requestId)
            throws IncorrectObjectException, WrongConditionException, IncorrectFieldException {
        log.info("Cancel request");
        return service.cancelRequest(userId, requestId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequestsByRequesterId(@PathVariable(value = "userId") Long userId)
            throws IncorrectObjectException {
        log.info("Get request by requester id");
        return service.getRequestsByRequesterId(userId);
    }
}
