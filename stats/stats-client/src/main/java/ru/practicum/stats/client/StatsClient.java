package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {
    private final WebClient webClient;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(@Value("${stats.server.url}") String host) {
        this.webClient = WebClient.create(host);
    }

    public void post(StatDto statDto) throws URISyntaxException {
        webClient.post()
                .uri("/hit")
                .bodyValue(statDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public List<StatOutputDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(StatOutputDto.class)
                .collectList()
                .block();
    }
}
