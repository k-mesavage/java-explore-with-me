package ru.practicum.mainservice.category.service;

import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto) throws IncorrectFieldException;
    CategoryDto updateCategory(Long catId, CategoryDto categoryDto) throws IncorrectObjectException, IncorrectFieldException;
    List<CategoryDto> getCategories(int from, int size);
    CategoryDto getCategoryById(Long catId) throws IncorrectObjectException;
    void deleteCategory(Long categoryId) throws IncorrectObjectException, IncorrectFieldException;
}
