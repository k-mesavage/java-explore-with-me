package ru.practicum.mainservice.user.service;

import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers(List<Long> ids, int from, int size);
    UserDto createUser(NewUserRequest newUserRequest);
    void deleteUserById(Long userId);
}
