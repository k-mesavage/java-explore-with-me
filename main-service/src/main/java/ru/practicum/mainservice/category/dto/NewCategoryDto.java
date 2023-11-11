package ru.practicum.mainservice.category.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Valid
public class NewCategoryDto {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
