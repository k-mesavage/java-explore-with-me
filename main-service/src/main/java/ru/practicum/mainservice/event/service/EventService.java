package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.util.enums.EventSort;
import ru.practicum.mainservice.util.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    EventFullDto updateEvent(UpdateEventRequestDto updateEventUserRequestDto, Long userId, Long eventId);

    EventFullDto cancelEvent(Long userId, Long eventId);

    EventFullDto getEventByInitiator(Long userId, Long eventId);

    List<EventFullDto> getEventsByInitiator(Long userId, int from, int size, HttpServletRequest request);

    List<EventFullDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  EventSort eventSort,
                                  int from,
                                  int size,
                                  HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequestDto updateEventAdminRequest);

    EventFullDto publishEventByAdmin(Long eventId);

    EventFullDto rejectEventByAdmin(Long eventId);

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<State> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size);
}
