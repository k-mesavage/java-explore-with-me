package ru.practicum.mainservice.util.checkers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryChecker {

    private final CategoryRepository categoryRepository;

    public void categoryExist(Long catId) {
        if (!categoryRepository.findAll().isEmpty()) {
            List<Long> ids = categoryRepository.findAll()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(catId)) {
                throw new ObjectNotFoundException("There is no category with id = " + catId);
            }
        } else {
            throw new ObjectNotFoundException("There is no category with such id = " + catId);
        }
    }
}
