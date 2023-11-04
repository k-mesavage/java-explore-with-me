package ru.practicum.mainservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationEventRepository;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationChecker {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;

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

    public void rePinned(Long compId) throws WrongConditionException {
        if (compilationRepository.getReferenceById(compId).getPinned()) {
            throw new WrongConditionException("Compilation with id " + compId + " already pinned");
        }
    }

    public void noPinned(Long compId) throws WrongConditionException {
        if (!compilationRepository.getReferenceById(compId).getPinned()) {
            throw new WrongConditionException("Compilation with id " + compId + " is no pinned");
        }
    }

    public void eventInCompilation(Long compId, Long eventId) {
        if (!compilationEventRepository.existsByCompilationIdAndEventId(compId, eventId)) {
            throw new IllegalArgumentException("Event id = " + eventId + " is not in compilation id = " + compId);
        }
    }
}
