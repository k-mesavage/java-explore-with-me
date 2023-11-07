package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.exception.ObjectNotFoundException;
import ru.practicum.mainservice.exception.WrongConditionException;
import ru.practicum.mainservice.util.constraints.Create;
import ru.practicum.mainservice.util.constraints.Update;

import javax.validation.Valid;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Validated(Create.class) NewCompilationDto newCompilationDto)
            throws IncorrectObjectException, ObjectNotFoundException {
        log.info("Add compilation {}", newCompilationDto);
        return service.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Validated(Update.class) @RequestBody NewCompilationDto compilationDto) throws ObjectNotFoundException {
        return service.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) throws IncorrectObjectException {
        log.info("Delete compilation with id {}", compId);
        service.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable Long compId,
                                      @PathVariable Long eventId) throws IncorrectObjectException, ObjectNotFoundException {
        log.info("Add event {} to compilation {}", eventId, compId);
        service.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventFromCompilation(@PathVariable Long compId,
                                           @PathVariable Long eventId) throws IncorrectObjectException, ObjectNotFoundException {
        log.info("Delete event {} from compilation {}", eventId, compId);
        service.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilationById(@PathVariable Long compId) throws WrongConditionException, IncorrectObjectException {
        log.info("Pin compilation {}", compId);
        service.pinCompilationById(compId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unPinCompilationById(@PathVariable Long compId) throws WrongConditionException, IncorrectObjectException {
        log.info("Unpin compilation {}", compId);
        service.unpinCompilationById(compId);
    }
}
