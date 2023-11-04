package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.model.CompilationEvent;
import ru.practicum.mainservice.compilation.repository.CompilationEventRepository;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.CompilationChecker;
import ru.practicum.mainservice.util.EventChecker;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationChecker compilationChecker;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationEventRepository compilationEventRepository;
    private final EventChecker eventChecker;
    private final EventService eventService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) throws IncorrectObjectException {
        for (Long eventId : newCompilationDto.getEvents()) {
            eventChecker.eventExist(eventId);
        }
        final Compilation newCompilation = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto));
        final Long compId = newCompilation.getId();
        for (Long eventId : newCompilationDto.getEvents()) {
            compilationEventRepository.save(new CompilationEvent(null, compId, eventId));
        }
        return compilationMapper.toDto(newCompilation, eventService.getEventsByCompilationId(compId));
    }

    @Override
    public CompilationDto getCompilationById(Long compId) throws IncorrectObjectException {
        compilationChecker.compilationExist(compId);
        return compilationMapper.toDto(
                compilationRepository.getReferenceById(compId), eventService.getEventsByCompilationId(compId));
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        return compilationRepository.findAllByParams(pinned, from, size).stream()
                .map(compilation -> compilationMapper.toDto(compilation, eventService.getEventsByCompilationId(compilation.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCompilationById(Long compId) throws IncorrectObjectException {
        compilationChecker.compilationExist(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) throws IncorrectObjectException {
        compilationChecker.compilationExist(compId);
        eventChecker.eventExist(eventId);
        compilationEventRepository.save(new CompilationEvent(null, compId, eventId));
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) throws IncorrectObjectException {
        compilationChecker.compilationExist(compId);
        eventChecker.eventExist(eventId);
        compilationChecker.eventInCompilation(compId, eventId);
        compilationEventRepository.deleteByCompilationIdAndEventId(compId, eventId);
    }

    @Override
    public void pinCompilationById(Long compId) throws IncorrectObjectException, WrongConditionException {
        compilationChecker.compilationExist(compId);
        compilationChecker.rePinned(compId);
        Compilation pinnedCompilation = compilationRepository.getReferenceById(compId);
        pinnedCompilation.setPinned(true);
        compilationRepository.save(pinnedCompilation);
    }

    @Override
    public void unpinCompilationById(Long compId) throws IncorrectObjectException, WrongConditionException {
        compilationChecker.compilationExist(compId);
        compilationChecker.noPinned(compId);
        Compilation pinnedCompilation = compilationRepository.getReferenceById(compId);
        pinnedCompilation.setPinned(false);
        compilationRepository.save(pinnedCompilation);
    }
}
