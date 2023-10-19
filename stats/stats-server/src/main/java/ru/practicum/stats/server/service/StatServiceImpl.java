package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.StatInputDto;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.server.mapper.StatMapper;
import ru.practicum.stats.server.model.Stat;
import ru.practicum.stats.server.repository.StatServerRepository;
import ru.practicum.stats.server.validation.DateValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatServerRepository repository;
    private final DateValidator validator;
    private final StatMapper mapper;

    public Stat create(StatInputDto statInputDto) {
        Stat result = Optional.of(repository.save(mapper.toStat(statInputDto))).orElseThrow();
        log.info("Hit {} added.", statInputDto);
        return result;
    }

    public List<StatOutputDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validator.dateValidation(start, end);
        List<StatOutputDto> result;
        if (unique) {
            result = repository.getUniqueStats(start, end, uris);
        } else {
            result = repository.getStats(start, end, uris);
        }
        log.info("Found {} endpoint hits", result.size());
        return result;
    }
}
