package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.util.enums.State;
import ru.practicum.mainservice.util.checkers.CategoryChecker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryChecker categoryChecker;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) throws IncorrectFieldException {
        Category newCategory = mapper.toCategory(newCategoryDto);
        try {
            newCategory = categoryRepository.save(newCategory);
            return mapper.toDto(newCategory);
        } catch (DataIntegrityViolationException ex) {
            throw new IncorrectFieldException("Data of category exception");
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        categoryChecker.checkCorrectParams(from, size);
        final List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).stream().collect(Collectors.toList());
        return mapper.toDtosList(categories);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) throws ObjectNotFoundException {
        categoryChecker.categoryExist(catId);
        return mapper.toDto(categoryRepository.getReferenceById(catId));
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) throws IncorrectFieldException, ObjectNotFoundException {
        categoryChecker.categoryExist(catId);
        categoryChecker.idIsNotBlank(catId);
        final Category updatedCategory = categoryRepository.getReferenceById(catId);
        if (categoryDto.getName() != null) {
            updatedCategory.setName(categoryDto.getName());
        }
        try {
            return mapper.toDto(categoryRepository.save(updatedCategory));
        } catch (DataIntegrityViolationException e) {
            throw new IncorrectFieldException("Name of category exception");
        }
    }

    @Override
    public void deleteCategory(Long categoryId) throws IncorrectFieldException {
        List<Event> events = eventRepository.findAllByCategoryIdInAndEventDateIsAfter(List.of(categoryId),
                LocalDateTime.now(), Pageable.unpaged());
        if (!events.isEmpty()) {
            boolean isConfirmedState = events.stream()
                    .anyMatch(e -> e.getState().equals(State.PUBLISHED));
            if (!isConfirmedState) {
                throw new IncorrectFieldException("Not delete. Category contains events");
            }
        }
        categoryRepository.deleteById(categoryId);
    }
}
