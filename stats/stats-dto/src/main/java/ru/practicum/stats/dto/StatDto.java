package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatDto {
    private Long id;
    @NotNull
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    @Length(min = 7, max = 15)
    private String ip;
    @NotNull
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")    private LocalDateTime timeStamp;

    public StatDto(String app, String uri, String ip, LocalDateTime timeStamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timeStamp = timeStamp;
    }
}
