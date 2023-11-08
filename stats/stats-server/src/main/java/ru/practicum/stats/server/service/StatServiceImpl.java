package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.server.mapper.StatMapper;
import ru.practicum.stats.server.model.Stat;
import ru.practicum.stats.server.repository.StatServerRepository;
import ru.practicum.stats.server.validation.DateValidator;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatServerRepository repository;
    private final StatMapper mapper;
    private final DateValidator dateValidator;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public StatDto create(StatDto statDto) {
        Stat result = repository.save(mapper.toStat(statDto));
        log.info("Hit {} added.", statDto);
        return mapper.toDto(result);
    }

    public List<StatOutputDto> get(String start, String end, List<String> uris, boolean unique) {
        List<StatOutputDto> result;
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);
        dateValidator.dateValidation(startTime, endTime);
        if (unique) {
            result = repository.getUniqueStats(LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter),
                    LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter), uris);
        } else {
            result = repository.getStats(LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter),
                    LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter), uris);
        }
        log.info("Found {} endpoint hits", result.size());
        return result;
    }
}
