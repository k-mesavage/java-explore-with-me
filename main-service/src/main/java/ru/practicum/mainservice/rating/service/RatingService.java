package ru.practicum.mainservice.rating.service;

import ru.practicum.mainservice.util.enums.RatingType;

public interface RatingService {
    void addRatingToEvent(Long eventId, Long userId, RatingType ratingType);
}
