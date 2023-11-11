package ru.practicum.mainservice.rating.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.rating.model.Rating;
import ru.practicum.mainservice.util.enums.RatingType;

@Service
public class RatingMapper {

    public Rating addLike(Long eventId, Long userId, Event event) {
        event.setRating(event.getRating() + 1);
        Rating rating = new Rating();
        rating.setEventId(eventId);
        rating.setUserId(userId);
        rating.setType(RatingType.LIKE);
        return rating;
    }

    public Rating addDislike(Long eventId, Long userId, Event event) {
        event.setRating(event.getRating() - 1);
        Rating rating = new Rating();
        rating.setEventId(eventId);
        rating.setUserId(userId);
        rating.setType(RatingType.DISLIKE);
        return rating;
    }

    public void removeRating(RatingType type, Event event) {
        if (type.equals(RatingType.LIKE)) {
            event.setRating(event.getRating() - 1);
        } else {
            event.setRating(event.getRating() + 1);
        }
    }
}
