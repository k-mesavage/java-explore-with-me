package ru.practicum.mainservice.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Valid
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                               @RequestParam(name = "from", defaultValue = "0") int from,
                                               @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Try get users which ids {}", ids);
        return ResponseEntity.ok().body(service.getUsers(ids, from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Try add User {}", newUserRequest.getEmail());
        return ResponseEntity.status(201).body(service.createUser(newUserRequest));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Try delete user {}", userId);
        service.deleteUserById(userId);
        return ResponseEntity.status(204).build();
    }
}
