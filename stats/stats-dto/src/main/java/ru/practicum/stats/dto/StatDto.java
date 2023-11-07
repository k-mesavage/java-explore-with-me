package ru.practicum.stats.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

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
    private String timeStamp;

    public StatDto(String app, String uri, String ip, String timeStamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timeStamp = timeStamp;
    }
}
