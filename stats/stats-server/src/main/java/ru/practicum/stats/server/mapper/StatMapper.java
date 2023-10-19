package ru.practicum.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.StatInputDto;
import ru.practicum.stats.server.model.Stat;

@Service
public class StatMapper {
    public Stat toStat(StatInputDto statInputDto) {
        Stat newStat = new Stat();
        newStat.setApp(statInputDto.getApp());
        newStat.setUri(statInputDto.getUri());
        newStat.setIp(statInputDto.getIp());
        newStat.setTimeStamp(statInputDto.getTimeStamp());
        return newStat;
    }
}
