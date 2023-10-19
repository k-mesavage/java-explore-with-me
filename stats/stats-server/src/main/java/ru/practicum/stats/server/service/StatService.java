package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.StatInputDto;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.server.model.Stat;

import java.time.LocalDateTime;
import java.util.List;


public interface StatService {
    Stat create(StatInputDto statInputDto);

    List<StatOutputDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
