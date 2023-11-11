package ru.practicum.mainservice.request.model;

import lombok.*;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.util.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class EventRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private Event event;

    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private State status;
}
