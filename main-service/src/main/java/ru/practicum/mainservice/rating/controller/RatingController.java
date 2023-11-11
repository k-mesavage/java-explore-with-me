package ru.practicum.mainservice.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.rating.service.RatingService;
import ru.practicum.mainservice.util.enums.RatingType;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events/{eventId}")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService service;

    @PatchMapping("/like")
    public void addLikeToEvent(@PathVariable Long eventId,
                               @PathVariable Long userId) {
        log.info("User {} add like to event {}", userId, eventId);
        service.addRatingToEvent(eventId, userId, RatingType.LIKE);
    }

    @PatchMapping("/dislike")
    public void addDislikeToEvent(@PathVariable Long eventId,
                                  @PathVariable Long userId) {
        log.info("User {} add dislike to event {}", userId, eventId);
        service.addRatingToEvent(eventId, userId, RatingType.DISLIKE);
    }

    @DeleteMapping("/rating")
    public void deleteUserRating(@PathVariable Long eventId,
                                 @PathVariable Long userId) {
        log.info("User {} delete rating from event {}", userId, eventId);
        service.removeUserRating(eventId, userId);
    }
}
