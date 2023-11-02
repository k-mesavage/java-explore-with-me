package ru.practicum.mainservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.StatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatCollector {

    private final StatsClient statsClient = new StatsClient("${stats-server.url}");

    public void collect(HttpServletRequest request) {
        StatDto statDto = StatDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timeStamp(LocalDateTime.now())
                .build();
        sendStat(statDto);
    }

    private void sendStat(StatDto statDto) {
        statsClient.post(statDto);
    }
}
