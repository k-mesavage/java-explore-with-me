package ru.practicum.mainservice.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.rating.model.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Rating getRatingByEventIdAndUserId(Long eventId, Long userId);

    List<Rating> findAllByEventId(Long eventId);
}
