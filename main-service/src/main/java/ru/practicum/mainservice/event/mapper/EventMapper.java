package ru.practicum.mainservice.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.user.dto.UserShortDto;
import ru.practicum.mainservice.util.EventChecker;
import ru.practicum.mainservice.util.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final EventChecker eventChecker;
    private final CategoryRepository categoryRepository;

    public Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setTitle(newEventDto.getTitle());
        event.setEventDate(newEventDto.getEventDate());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        event.setViews(0L);
        event.setPublishedOn(LocalDateTime.now());
        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(new Category(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews()).build();
    }

    public List<EventFullDto> toListOfEventFullDto(Iterable<Event> events) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtos.add(toEventFullDto(event));
        }
        return eventFullDtos;
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public List<EventShortDto> toListOfEventShortDto(Iterable<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(toEventShortDto(event));
        }
        return eventShortDtos;
    }

}
