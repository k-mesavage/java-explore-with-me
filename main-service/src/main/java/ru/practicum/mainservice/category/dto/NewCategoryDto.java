package ru.practicum.mainservice.category.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Valid
public class NewCategoryDto {
    @NotBlank
    private String name;
}
