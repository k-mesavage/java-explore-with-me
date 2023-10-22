package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;

import java.time.LocalDateTime;
import java.util.List;


public interface StatService {
    StatDto create(StatDto statDto);

    List<StatOutputDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
