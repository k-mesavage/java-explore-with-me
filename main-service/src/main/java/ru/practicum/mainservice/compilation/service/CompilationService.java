package ru.practicum.mainservice.compilation.service;

import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws IncorrectObjectException;

    CompilationDto getCompilationById(Long compId) throws IncorrectObjectException;

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    void deleteCompilationById(Long compId) throws IncorrectObjectException;

    void addEventToCompilation(Long compId, Long eventId) throws IncorrectObjectException;

    void deleteEventFromCompilation(Long compId, Long eventId) throws IncorrectObjectException;

    void pinCompilationById(Long compId) throws IncorrectObjectException, WrongConditionException;

    void unpinCompilationById(Long compId) throws IncorrectObjectException, WrongConditionException;
}
