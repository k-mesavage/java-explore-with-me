package ru.practicum.mainservice.compilation.service;

import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws IncorrectObjectException, ObjectNotFoundException;
    CompilationDto updateCompilation(Long compId, NewCompilationDto compilationDto) throws ObjectNotFoundException;
    CompilationDto getCompilationById(Long compId) throws IncorrectObjectException, ObjectNotFoundException;

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    void deleteCompilationById(Long compId) throws IncorrectObjectException;
}
