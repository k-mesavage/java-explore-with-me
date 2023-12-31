package ru.practicum.mainservice.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.rating.mapper.RatingMapper;
import ru.practicum.mainservice.rating.model.Rating;
import ru.practicum.mainservice.rating.repository.RatingRepository;
import ru.practicum.mainservice.util.checkers.EventChecker;
import ru.practicum.mainservice.util.checkers.RatingChecker;
import ru.practicum.mainservice.util.enums.RatingType;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final EventChecker eventChecker;
    private final EventRepository eventRepository;
    private final RatingChecker ratingChecker;
    private final RatingMapper ratingMapper;
    private final RatingRepository ratingRepository;

    @Override
    public void addRatingToEvent(Long eventId, Long userId, RatingType ratingType) {
        Event event = eventRepository.getReferenceById(eventId);
        eventChecker.eventPublished(event.getId());
        eventChecker.eventInitiatorIsNot(eventId, userId);
        ratingChecker.reRate(eventId, userId);
        Rating rating;
        if (ratingType.equals(RatingType.LIKE)) {
            rating = ratingMapper.addLike(eventId, userId);
        } else {
            rating = ratingMapper.addDislike(eventId, userId);
        }
        eventRepository.save(event);
        ratingRepository.save(rating);
    }
}
