package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public StatDto post(@RequestBody StatDto statDto) {
        return service.create(statDto);
    }

    @GetMapping("/stats")
    public List<StatOutputDto> get(@RequestParam @DateTimeFormat(pattern = dateTimeFormat) LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = dateTimeFormat) LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        return service.get(start, end, uris, unique);
    }
}
