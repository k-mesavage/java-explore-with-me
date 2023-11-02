package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.server.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService service;
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StatDto> post(@RequestBody StatDto statDto) {
        log.info("Add hit {}", statDto);
        return ResponseEntity.status(201).body(service.create(statDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatOutputDto>> get(@RequestParam @DateTimeFormat(pattern = dateTimeFormat) LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = dateTimeFormat) LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get hits");
        return ResponseEntity.ok().body(service.get(start, end, uris, unique));
    }
}
