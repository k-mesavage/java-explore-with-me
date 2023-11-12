package ru.practicum.mainservice.rating.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.rating.model.Rating;
import ru.practicum.mainservice.util.enums.RatingType;

@Service
public class RatingMapper {

    public Rating addLike(Long eventId, Long userId) {
        Rating rating = new Rating();
        rating.setEventId(eventId);
        rating.setUserId(userId);
        rating.setType(RatingType.LIKE);
        rating.setValue(1);
        return rating;
    }

    public Rating addDislike(Long eventId, Long userId) {
        Rating rating = new Rating();
        rating.setEventId(eventId);
        rating.setUserId(userId);
        rating.setType(RatingType.DISLIKE);
        rating.setValue(-1);
        return rating;
    }
}
