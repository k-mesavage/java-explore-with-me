package ru.practicum.stats.server.validation;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DateValidator {
    public void dateValidation(LocalDateTime start, LocalDateTime end) {
        if (!(start.isBefore(end) && !start.equals(end))) {
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }
    }
}
