package ru.practicum.mainservice.rating.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainservice.util.enums.RatingType;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "ratings")

public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "type")
    private RatingType type;

    @Column(name = "value")
    private int value;
}
