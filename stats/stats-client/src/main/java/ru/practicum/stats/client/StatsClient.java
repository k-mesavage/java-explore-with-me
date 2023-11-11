package ru.practicum.stats.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.StatDto;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.dto.StatsDtoToGetStats;
import static ru.practicum.stats.client.constants.DateTimeConstant.DATE_TIME_FORMAT;


import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    WebClient webClient = WebClient.create();

    final String uri = "http://stats-server:9090";

    public void saveHit(String path, StatDto statsDtoToSave) {
        webClient
                .method(HttpMethod.POST)
                .uri(uri + path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(statsDtoToSave))
                .retrieve()
                .toEntity(String.class)
                .subscribe(responseEntity -> {
                    System.out.println("Статус код: " + responseEntity.getStatusCode());
                    System.out.println("Ответ: " + responseEntity.getBody());
                });
    }

    public List<StatOutputDto> getStatistics(StatsDtoToGetStats statsParameters) {
        String endpointPath = "/stats";

        String url = UriComponentsBuilder.fromHttpUrl(uri + endpointPath)
                .queryParam("start", statsParameters.getStart())
                .queryParam("end", statsParameters.getEnd())
                .queryParam("uris", statsParameters.getUris())
                .queryParam("unique", statsParameters.isUnique())
                .queryParam("from", statsParameters.getFrom())
                .queryParam("size", statsParameters.getSize())
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(StatOutputDto.class)
                .collectList()
                .block();
    }
}
