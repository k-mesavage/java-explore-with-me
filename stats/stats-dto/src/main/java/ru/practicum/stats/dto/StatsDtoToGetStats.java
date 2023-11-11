package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoToGetStats {
    private String start;
    private String end;
    private List<String> uris;
    private boolean unique;
    private Integer from;
    private Integer size;
}