package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        final CategoryDto categoryDto = service.addCategory(newCategoryDto);
        log.info("Add category {}", categoryDto);
        return categoryDto;
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable @NotNull Long catId,
            @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Update category {}", categoryDto);
        categoryDto = service.updateCategory(catId, categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Delete category with id {}", catId);
        service.deleteCategory(catId);
    }
}
