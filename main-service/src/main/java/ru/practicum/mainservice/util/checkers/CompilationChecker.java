package ru.practicum.mainservice.util.checkers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.exception.IncorrectObjectException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationChecker {

    private final CompilationRepository compilationRepository;

    public void compilationExist(Long compId) throws IncorrectObjectException {
        final List<Compilation> allCompilations = compilationRepository.findAll();
        List<Long> ids = new ArrayList<>();
        if (!allCompilations.isEmpty()) {
            ids = allCompilations.stream()
                    .map(Compilation::getId)
                    .collect(Collectors.toList());
        }
        if (allCompilations.isEmpty() || !ids.contains(compId)) {
            throw new IncorrectObjectException("No compilation with id " + compId);
        }
    }
}
