package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;

import java.util.List;


public interface StatService {
    StatDto create(StatDto statDto);

    List<StatOutputDto> get(String start, String end, List<String> uris, boolean unique);
}
