package ru.practicum.mainservice.util.checkers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.rating.repository.RatingRepository;

@Service
@RequiredArgsConstructor
public class RatingChecker {

    private final RatingRepository ratingRepository;

    public void reRate(Long eventId, Long userId) {
        if (ratingRepository.getRatingByEventIdAndUserId(eventId, userId) != null) {
            throw new IncorrectFieldException("Re-add rating exception");
        }
    }
}
