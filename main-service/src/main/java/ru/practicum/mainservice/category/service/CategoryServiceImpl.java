package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.util.CategoryChecker;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryChecker categoryChecker;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = mapper.toCategory(newCategoryDto);
        try {
            newCategory = categoryRepository.save(newCategory);
            return mapper.toDto(newCategory);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Data of category exception");
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        categoryChecker.checkCorrectParams(from, size);
        final List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).stream().collect(Collectors.toList());
        return mapper.toDtosList(categories);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) throws IncorrectObjectException {
        categoryChecker.categoryExist(catId);
        return mapper.toDto(categoryRepository.getReferenceById(catId));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) throws IncorrectObjectException, IncorrectFieldException {
        categoryChecker.categoryExist(categoryDto.getId());
        categoryChecker.idIsNotBlank(categoryDto.getId());
        final Category updatedCategory = categoryRepository.getReferenceById(categoryDto.getId());
        updatedCategory.setName(categoryDto.getName());
        try {
            return mapper.toDto(categoryRepository.save(updatedCategory));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Name of category exception");
        }
    }

    //TODO***
    @Override
    public void deleteCategory(Long categoryId) throws IncorrectObjectException {
        categoryChecker.categoryExist(categoryId);
        categoryRepository.deleteById(categoryId);
    }
}
