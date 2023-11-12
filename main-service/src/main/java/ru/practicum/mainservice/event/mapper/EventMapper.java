package ru.practicum.mainservice.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequestDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.rating.model.Rating;
import ru.practicum.mainservice.rating.repository.RatingRepository;
import ru.practicum.mainservice.user.dto.UserShortDto;
import ru.practicum.mainservice.util.enums.RatingType;
import ru.practicum.mainservice.util.enums.State;
import ru.practicum.mainservice.util.enums.StateAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;

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
        event.setRating(event.getRating());
        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        event.setRating(getEventRating(event.getId()));
        event.setLikes(getLikes(event.getId()));
        event.setDislikes(getDislikes(event.getId()));
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
                .views(event.getViews())
                .rating(event.getRating())
                .likes(event.getLikes())
                .dislikes(event.getDislikes())
                .build();
    }

    public List<EventFullDto> toListOfEventFullDto(List<Event> events) {
        return events.stream().map(this::toEventFullDto).collect(Collectors.toList());
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId().intValue())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public Event updateFields(Event event, UpdateEventRequestDto requestDto) {
        if (requestDto.getStateAction() != null) {
            if (requestDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }
            if (requestDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
            }
        }
        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getEventDate() != null && requestDto.getEventDate()
                .isAfter(event.getEventDate())) {
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
        return event;
    }

    private int getEventRating(Long eventId) {
        final List<Rating> allRatings = ratingRepository.findAllByEventId(eventId);
        AtomicInteger rating = new AtomicInteger();
        allRatings.stream().filter(r -> r.getType().equals(RatingType.LIKE)).forEach(r -> rating.getAndIncrement());
        allRatings.stream().filter(r -> r.getType().equals(RatingType.DISLIKE)).forEach(r -> rating.getAndDecrement());
        return rating.get();
    }

    private int getLikes(Long eventId) {
        final List<Rating> allRatings = ratingRepository.findAllByEventId(eventId);
        AtomicInteger rating = new AtomicInteger();
        allRatings.stream().filter(r -> r.getType().equals(RatingType.LIKE)).forEach(r -> rating.getAndIncrement());
        return rating.get();
    }

    private int getDislikes(Long eventId) {
        final List<Rating> allRatings = ratingRepository.findAllByEventId(eventId);
        AtomicInteger rating = new AtomicInteger();
        allRatings.stream().filter(r -> r.getType().equals(RatingType.DISLIKE)).forEach(r -> rating.getAndIncrement());
        return rating.get();
    }
}

