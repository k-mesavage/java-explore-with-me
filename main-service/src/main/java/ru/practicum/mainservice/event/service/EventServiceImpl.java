package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.*;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.rating.repository.RatingRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.checkers.CategoryChecker;
import ru.practicum.mainservice.util.checkers.EventChecker;
import ru.practicum.mainservice.util.checkers.UserChecker;
import ru.practicum.mainservice.util.enums.EventSort;
import ru.practicum.mainservice.util.enums.State;
import ru.practicum.mainservice.util.enums.StateAction;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.dto.StatsDtoToGetStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.util.DateTimeConstant.DATE_TIME_FORMAT;

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final StatsClient statsClient = new StatsClient();
    private final RatingRepository ratingRepository;

    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        final Long categoryId = newEventDto.getCategory();
        eventChecker.isEventDateBeforeTwoHours(newEventDto.getEventDate());
        userChecker.checkUserExists(userId);
        categoryChecker.categoryExist(categoryId);
        final Event event = eventMapper.toEvent(newEventDto);
        final User initiator = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User not found"));
        final Category category = categoryRepository.getReferenceById(categoryId);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(State.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEvent(UpdateEventRequestDto requestDto, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        eventChecker.notPublished(event.getState());
        StateAction stateAction = requestDto.getStateAction();
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        if (requestDto.getEventDate() != null) {
            eventChecker.isEventDateBeforeTwoHours(requestDto.getEventDate());
        }
        if (requestDto.getStateAction() != null) {
            eventChecker.eventNotPublished(event);
            eventChecker.eventPublished(event);
            if (event.getState().equals(State.CANCELED) && stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
                return eventMapper.toEventFullDto(eventRepository.save(event));
            }
            event = eventMapper.updateFields(event, requestDto);
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        Event event = eventRepository.getReferenceById(eventId);
        event.setState(State.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByInitiator(Long userId, Long eventId) {
        userChecker.checkUserExists(userId);
        eventChecker.eventExist(eventId);
        eventChecker.eventInitiator(eventId, userId);
        Event event = eventRepository.findAllByInitiatorIdAndId(userId, eventId);
        event.setViews(event.getViews());
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsByInitiator(Long userId, int from, int size, HttpServletRequest request) {
        userChecker.checkUserExists(userId);
        statsClient.saveHit("/hit", getStatsDtoToSave(request));
        return eventMapper.toListOfEventFullDto(eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size)));
    }

    @Override
    public List<EventFullDto> getEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        String rangeStart,
                                        String rangeEnd,
                                        Boolean onlyAvailable,
                                        EventSort sort,
                                        int from,
                                        int size,
                                        HttpServletRequest request) {
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
        statsClient.saveHit("/hit", getStatsDtoToSave(request));
        List<Event> eventsWithViews = getEventViewsList(events);
        return eventMapper.toListOfEventFullDto(eventsWithViews);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.getByIdPublished(eventId);
        if (event != null) {
            event.setViews(getViews(event));
            statsClient.saveHit("/hit", getStatsDtoToSave(request));
            return eventMapper.toEventFullDto(event);
        } else throw new ObjectNotFoundException("Event not found");
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequestDto requestDto) {
        eventChecker.eventExist(eventId);
        if (requestDto.getEventDate() != null) {
            eventChecker.isEventDateBeforeTwoHours(requestDto.getEventDate());
        }
        Event event = eventRepository.getReferenceById(eventId);
        eventChecker.statusForAdminUpdate(event, requestDto);
        if (requestDto.getStateAction() != null) {
            if (requestDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
                eventRepository.save(event);
                return eventMapper.toEventFullDto(eventRepository.save(event));
            }
        }
        event = eventMapper.updateFields(event, requestDto);
        event.setViews(getViews(event));
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto publishEventByAdmin(Long eventId) {
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
    public EventFullDto rejectEventByAdmin(Long eventId) {
        eventChecker.eventExist(eventId);
        Event event = eventRepository.getReferenceById(eventId);
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

    private StatDto getStatsDtoToSave(HttpServletRequest request) {
        return new StatDto(
                "ewm-main",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(formatter));
    }

    private StatsDtoToGetStats getStatsDtoToGetStats(List<String> uris, boolean unique, Integer from, Integer size) {
        return new StatsDtoToGetStats(
                "2020-05-05 00:00:00",
                "2025-01-01 00:00:00",
                uris,
                unique,
                from == null ? 0 : from,
                size == null ? 10 : size
        );
    }

    private Long getViews(Event event) {
        String eventUri = "/events/" + event.getId();
        StatsDtoToGetStats statsDtoToGetStats = getStatsDtoToGetStats(List.of(eventUri), true, null, null);
        List<StatOutputDto> statsList = statsClient.getStatistics(statsDtoToGetStats);
        return statsList.isEmpty() ? 0 : statsList.get(0).getHits();
    }

    private List<Event> getEventViewsList(List<Event> events) {
    List<Event> getEventViewsList(List<Event> events) {
        String eventUri = "/events/";
        List<String> uriEventList = events.stream()
                .map(e -> eventUri + e.getId().toString())
                .collect(Collectors.toList());
        StatsDtoToGetStats statsDtoToGetStats = getStatsDtoToGetStats(uriEventList, true, null, null);
        List<StatOutputDto> statsList = statsClient.getStatistics(statsDtoToGetStats);

        Map<Long, Long> eventViewsMap = getEventHitsMap(statsList, events);

        events.stream().filter(e -> eventViewsMap.containsKey(e.getId())).forEach(e -> e.setViews(eventViewsMap.get(e.getId())));
        return events;
    }

    private Map<Long, Long> getEventHitsMap(List<StatOutputDto> hitDtoList, List<Event> events) {
        Map<Long, Long> hits = new HashMap<>();
        if (hitDtoList.isEmpty()) {
            events.forEach(e -> hits.put(e.getId(), 0L));
        } else {

            hitDtoList.forEach(s -> hits.put(
                    (long) Integer.parseInt(s.getUri().replace("/events/", "")), s.getHits()));
        }
        return hits;
    }
}
