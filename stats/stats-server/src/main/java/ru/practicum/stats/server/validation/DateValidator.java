package ru.practicum.stats.server.validation;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DateValidator {

    public void dateValidation(LocalDateTime start, LocalDateTime end) {

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }
    }
}
