package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.model.CompilationEvent;
import ru.practicum.mainservice.compilation.repository.CompilationEventRepository;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.checker.CompilationChecker;
import ru.practicum.mainservice.util.checker.EventChecker;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationChecker compilationChecker;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationEventRepository compilationEventRepository;
    private final EventChecker eventChecker;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.getAllByIds(newCompilationDto.getEvents());
        }
        final Compilation newCompilation = compilationMapper.toCompilation(newCompilationDto, events);
        return compilationMapper.toDto(compilationRepository.save(newCompilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compId, NewCompilationDto compilationDto) throws ObjectNotFoundException {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Compilation not found"));
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.getAllByIds(compilationDto.getEvents()));
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        return compilationMapper.toDtosList(compilationRepository.findAllByPinnedIs(pinned, PageRequest.of(from, size)));

    }

    @Override
    public void deleteCompilationById(Long compId) throws IncorrectObjectException {
        compilationChecker.compilationExist(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) throws IncorrectObjectException, ObjectNotFoundException {
        compilationChecker.compilationExist(compId);
        eventChecker.eventExist(eventId);
        compilationEventRepository.save(new CompilationEvent(null, compId, eventId));
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) throws IncorrectObjectException, ObjectNotFoundException {
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
