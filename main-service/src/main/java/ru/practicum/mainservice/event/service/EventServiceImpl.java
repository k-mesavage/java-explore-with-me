package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.compilation.repository.CompilationEventRepository;
import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.*;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventChecker eventChecker;
    private final EventRepository eventRepository;
    private final UserChecker userChecker;
    private final CategoryChecker categoryChecker;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient = new StatsClient("http://stats-server:9090");

    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId)
            throws IncorrectObjectException, WrongConditionException {
        final Long categoryId = newEventDto.getCategory();
        eventChecker.isEventDateBeforeTwoHours(newEventDto.getEventDate());
        userChecker.checkUserExists(userId);
        categoryChecker.categoryExist(categoryId);
        final Event event = eventMapper.toEvent(newEventDto);
        final User initiator = userRepository.findById(userId).get();
        final Category category = categoryRepository.getReferenceById(categoryId);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(State.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEvent(UpdateEventRequestDto requestDto, Long userId, Long eventId)
            throws IncorrectObjectException, IncorrectFieldException, WrongConditionException, ru.practicum.mainservice.exception.ObjectNotFoundException {
        Event event = eventRepository.getReferenceById(eventId);
        StateAction stateAction = requestDto.getStateAction();
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);

        if (requestDto.getStateAction() != null) {
            eventChecker.eventPublished(event);
            if (event.getState().equals(State.CANCELED) && stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
                return eventMapper.toEventFullDto(eventRepository.save(event));
            }
            updateFields(event, requestDto);
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ru.practicum.mainservice.exception.ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        Event event = eventRepository.getReferenceById(eventId);
        event.setState(State.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByInitiator(Long userId, Long eventId) throws IncorrectObjectException, IncorrectFieldException, ru.practicum.mainservice.exception.ObjectNotFoundException {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiatorIsNot(eventId, userId);
        return eventMapper.toEventFullDto(eventRepository.findAllByInitiatorIdAndId(userId, eventId));
    }

    @Override
    public List<EventFullDto> getEventsByInitiator(Long userId, int from, int size) throws IncorrectObjectException {
        userChecker.checkUserExists(userId);
        eventChecker.checkCorrectParams(from, size);
        return eventMapper.toListOfEventFullDto(eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size)));
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         ru.practicum.mainservice.util.EventSort sort,
                                         int from,
                                         int size,
                                         HttpServletRequest request) throws WrongConditionException, URISyntaxException {
        eventChecker.checkCorrectParams(from, size);
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (rangeStart == null) {
            startTime = LocalDateTime.now();
        } else {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeStart == null) {
            endTime = LocalDateTime.now().plusYears(100);
        } else {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (startTime.isAfter(endTime)) {
            throw new WrongConditionException("Wrong condition of ranges");
        }
        List<Event> events;
        if (categories == null) {
            if (onlyAvailable) {
                events = eventRepository.findEventsByManyParamsWithoutCatAvailable(
                        text, paid, startTime, endTime, from, size);
            } else {
                events = eventRepository.findEventsByManyParamsWithoutCat(
                        text, paid, startTime, endTime, from, size);
            }
        } else {
            if (onlyAvailable) {
                events = eventRepository.findEventsByManyParamsWithCatAvailable(
                        text, categories, paid, startTime, endTime, from, size);
            } else {
                events = eventRepository.findEventsByManyParamsWithCat(
                        text, categories, paid, startTime, endTime, from, size);
            }
        }
        if (sort != null) {
            if (sort == EventSort.EVENT_DATE) {
                events = events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
            }
            if (sort == EventSort.VIEWS) {
                events = events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
            }
        }
        statsClient.post(new StatDto("event-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));

        addViewsForEvents(events);
        return eventMapper.toListOfEventShortDto(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) throws URISyntaxException, ObjectNotFoundException {
        final Event event = eventRepository.getByIdPublished(eventId);
        if (event != null) {
            statsClient.post(new StatDto("event-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
            event.setViews(getViews(event));
            return eventMapper.toEventFullDto(event);
        } else throw new ObjectNotFoundException("Event not found");
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequestDto requestDto)
            throws WrongConditionException, ru.practicum.mainservice.exception.ObjectNotFoundException, IncorrectFieldException {
        eventChecker.eventExist(eventId);
        if (requestDto.getEventDate() != null) {
            eventChecker.isEventDateBeforeTwoHours(requestDto.getEventDate());
        }
        final Event event = eventRepository.getReferenceById(eventId);
        eventChecker.statusForAdminUpdate(event, requestDto);
        if (requestDto.getStateAction() != null) {
            if (requestDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
                eventRepository.save(event);
                return eventMapper.toEventFullDto(eventRepository.save(event));
            }
        }
        updateFields(event, requestDto);
        event.setState(State.PUBLISHED);
        event.setViews(getViews(event));
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEventByAdmin(Long eventId)
            throws WrongConditionException, ru.practicum.mainservice.exception.ObjectNotFoundException {
        eventChecker.eventExist(eventId);
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new WrongConditionException("Start time of event must be at least 1 hour from now");
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new WrongConditionException("Event must be in state PENDING to be published");
        }
        event.setState(State.PUBLISHED);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEventByAdmin(Long eventId)
            throws WrongConditionException, ru.practicum.mainservice.exception.ObjectNotFoundException {
        eventChecker.eventExist(eventId);
        Event event = eventRepository.getById(eventId);
        if (!event.getState().equals(State.PENDING)) {
            throw new WrongConditionException("Event must not be in state PENDING to be rejected");
        }
        event.setState(State.CANCELLING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<State> states,
                                               List<Long> categories,
                                               String rangeStart,
                                               String rangeEnd,
                                               int from,
                                               int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events;
        if (rangeStart != null & rangeEnd != null) {
            LocalDateTime startEvens = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime endEvens = LocalDateTime.parse(rangeEnd, formatter);
            events = eventRepository.findEventsByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfterAndEventDateIsBefore(
                    users,
                    states,
                    categories,
                    startEvens,
                    endEvens,
                    pageable);
        } else {
            if (users != null) {
                events = eventRepository.findAllByCategoryIdInAndEventDateIsAfter(categories, LocalDateTime.now(), pageable);
                return eventMapper.toListOfEventFullDto(events);
            }
            if (categories != null) {
                events = eventRepository.findAllByStateInAndEventDateIsAfter(states, LocalDateTime.now(), pageable);
            } else {
                events = eventRepository.findAllByEventDateIsAfter(LocalDateTime.now(), pageable);
            }
        }
        return eventMapper.toListOfEventFullDto(events);
    }

    @Override
    public List<EventShortDto> getEventsByCompilationId(Long compilationId) {
        List<Long> eventsIds = compilationEventRepository.getCompilationEventIds(compilationId);
        return eventMapper.toListOfEventShortDto(eventRepository.getAllByIds(eventsIds));
    }

    private void addViewsForEvents(List<Event> events) {
        for (Event event : events) {
            event.setViews(getViews(event));
            eventRepository.save(event);
        }
    }

    private Long getViews(Event event) {
        String eventUri = "/events/" + event.getId();
        List<StatOutputDto> stats = statsClient.get(null, null, List.of(eventUri), true);
        return stats.isEmpty() ? 0 : stats.get(0).getHits();
    }

    private void updateFields(Event event, UpdateEventRequestDto requestDto) throws WrongConditionException {
        if (requestDto.getStateAction() != null) {
        if (requestDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) event.setState(State.CANCELED);
        if (requestDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) event.setState(State.PUBLISHED);
    }
        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getEventDate() != null) {
            eventChecker.isEventDateBeforeTwoHours(requestDto.getEventDate());
            event.setEventDate(requestDto.getEventDate());
        }
        if (requestDto.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(requestDto.getCategory()));
        }
        if (requestDto.getDescription() != null) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getTitle() != null) {
            event.setTitle(requestDto.getTitle());
        }
    }
}
