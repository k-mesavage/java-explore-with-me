package ru.practicum.mainservice.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService service;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> findCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get compilations to public");
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllCompilations(pinned, from, size));
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Get compilation with id {} to public", compId);
        return service.getCompilationById(compId);
    }
}
