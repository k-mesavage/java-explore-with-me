package ru.practicum.mainservice.event.service;

import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.enums.EventSort;
import ru.practicum.mainservice.util.enums.State;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId) throws IncorrectObjectException, SQLException, WrongConditionException, ObjectNotFoundException;

    EventFullDto updateEvent(UpdateEventRequestDto updateEventUserRequestDto, Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;

    EventFullDto cancelEvent(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException;

    EventFullDto getEventByInitiator(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ObjectNotFoundException;

    List<EventFullDto> getEventsByInitiator(Long userId, int from, int size, HttpServletRequest request) throws IncorrectObjectException;

    List<EventFullDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  EventSort eventSort,
                                  int from,
                                  int size,
                                  HttpServletRequest request) throws WrongConditionException, URISyntaxException;

    EventFullDto getEventById(Long eventId, HttpServletRequest request) throws IncorrectObjectException, URISyntaxException, ObjectNotFoundException;

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequestDto updateEventAdminRequest) throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ObjectNotFoundException;

    EventFullDto publishEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException, ObjectNotFoundException;

    EventFullDto rejectEventByAdmin(Long eventId) throws IncorrectObjectException, WrongConditionException, ObjectNotFoundException;

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<State> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size);
}
