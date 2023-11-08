package ru.practicum.mainservice.category.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.model.Category;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryMapper {

    public Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public List<CategoryDto> toDtosList(List<Category> categories) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category c : categories) {
            categoryDtoList.add(toDto(c));
        }
        return categoryDtoList;
    }
}
