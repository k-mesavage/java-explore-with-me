package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component("statsClient")
@RequiredArgsConstructor
public class StatsClient {
    private final WebClient webClient;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String host) {
        this.webClient = WebClient.create(host);
    }

    public StatDto post(StatDto statDto) {
        return webClient.post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(statDto)
                .retrieve()
                .bodyToMono(StatDto.class)
                .block();
    }

    public List<StatOutputDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(StatOutputDto.class)
                .collectList()
                .block();
    }
}
