package ru.practicum.mainservice.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String email;
    private long id;
    private String name;
}
