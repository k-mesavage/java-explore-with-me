package ru.practicum.mainservice.compilation.service;

import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, NewCompilationDto compilationDto);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    void deleteCompilationById(Long compId);
}
