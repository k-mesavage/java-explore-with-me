package ru.practicum.mainservice.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRatingDto {

    private Long eventId;

    private Long userId;
}
