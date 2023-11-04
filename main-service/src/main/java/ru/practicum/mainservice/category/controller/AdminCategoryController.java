package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        final CategoryDto categoryDto = service.addCategory(newCategoryDto);
        log.info("Add category {}", categoryDto);
        return categoryDto;
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto)
            throws IncorrectObjectException, IncorrectFieldException {
        log.info("Update category {}", categoryDto);
        categoryDto = service.updateCategory(categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Long catId) throws IncorrectObjectException {
        log.info("Delete category with id {}", catId);
        service.deleteCategory(catId);
    }
}
