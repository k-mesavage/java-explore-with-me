package ru.practicum.mainservice.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private String email;
    private long id;
    private String name;
}
