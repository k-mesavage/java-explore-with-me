package ru.practicum.mainservice.user.model;

import lombok.*;
import reactor.util.annotation.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    @NonNull
    private String name;
    @Column(name = "email")
    @NonNull
    private String email;
}
