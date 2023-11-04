package ru.practicum.mainservice.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    @NotBlank
    private String email;
    private long id;
    @NotBlank
    private String name;
}
