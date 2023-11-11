package ru.practicum.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.server.model.Stat;

@Service
public class StatMapper {
    public Stat toStat(StatDto statDto) {
        Stat result = new Stat();
        result.setApp(statDto.getApp());
        result.setUri(statDto.getUri());
        result.setIp(statDto.getIp());
        return result;
    }

    public StatDto toDto(Stat stat) {
        return new StatDto(stat.getId(), stat.getApp(), stat.getUri(), stat.getIp(), stat.getTimeStamp().toString());
    }
}
