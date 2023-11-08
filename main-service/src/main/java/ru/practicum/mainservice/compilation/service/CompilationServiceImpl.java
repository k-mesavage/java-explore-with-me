package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.util.checkers.CompilationChecker;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationChecker compilationChecker;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = List.of();
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.getAllByIdIn(newCompilationDto.getEvents());
        }
        Compilation newCompilation = compilationMapper.toCompilation(newCompilationDto, events);
        return compilationMapper.toDto(compilationRepository.save(newCompilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compId, NewCompilationDto compilationDto) throws ObjectNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Compilation not found"));
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.getAllByIdIn(compilationDto.getEvents()));
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        compilation.setPinned(compilationDto.isPinned());
        compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) throws ObjectNotFoundException {
        final Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Compilation not found"));
        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        return compilationMapper.toDtosList(compilationRepository.findAllByParams(pinned, from, size));

    }

    @Override
    public void deleteCompilationById(Long compId) throws IncorrectObjectException {
        compilationChecker.compilationExist(compId);
        compilationRepository.deleteById(compId);
    }
}
