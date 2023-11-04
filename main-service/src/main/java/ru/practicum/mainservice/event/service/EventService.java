package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.EventSort;

import java.sql.SQLException;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId) throws IncorrectFieldException, IncorrectObjectException, SQLException;

    EventFullDto updateEvent(UpdateEventUserRequestDto updateEventUserRequestDto, Long userId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException;

    EventFullDto cancelEvent(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException;

    EventFullDto getEventByInitiator(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException;

    List<EventFullDto> getEventsByInitiator(Long userId, int from, int size) throws IncorrectObjectException;

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  EventSort eventSort,
                                  int from,
                                  int size);

    EventFullDto getEventById(Long eventId) throws IncorrectObjectException;

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequest) throws IncorrectObjectException, IncorrectFieldException;

    EventFullDto publishEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException;

    EventFullDto rejectEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException;

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size);

    List<EventShortDto> getEventsByCompilationId(Long compilationId);

    void addViewsForEvents(List<EventShortDto> eventsShortDtos);

    void addViewForEvent(EventFullDto eventFullDto);
}
