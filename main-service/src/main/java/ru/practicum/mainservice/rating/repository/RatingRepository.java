package ru.practicum.mainservice.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.rating.model.Rating;
import ru.practicum.mainservice.util.enums.RatingType;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Long countAllByEventIdAndType(Long eventId, RatingType type);

    Rating getRatingByEventIdAndUserId(Long eventId, Long userId);
}
