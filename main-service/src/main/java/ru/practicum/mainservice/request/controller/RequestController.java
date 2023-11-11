package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.request.dto.EventRequestDto;
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
    public EventRequestDto addRequest(@PathVariable("userId") Long userId,
                                      @RequestParam ("eventId") Long eventId) {
        log.info("Add Request");
        return service.createRequest(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@Valid @RequestBody EventRequestStatusUpdateRequest request,
                                                              @PathVariable(value = "userId") long userId,
                                                              @PathVariable(value = "eventId") long eventId) {
        log.info("Path request status");
        return service.patchRequest(request, userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public EventRequestDto cancelRequest(@PathVariable(value = "userId") Long userId,
                                         @PathVariable(value = "requestId") Long requestId) {
        log.info("Cancel request");
        return service.cancelRequest(userId, requestId);
    }

    @GetMapping("/requests")
    public List<EventRequestDto> getRequestsByRequesterId(@PathVariable(value = "userId") Long userId) {
        log.info("Get request by requester id");
        return service.getRequestsByRequesterId(userId);
    }
}
