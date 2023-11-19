package ru.practicum.mainservice.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.mainservice.util.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "comment_state")
    private State state;

}
