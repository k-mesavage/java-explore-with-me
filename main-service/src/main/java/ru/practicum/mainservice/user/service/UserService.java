package ru.practicum.mainservice.user.service;

import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest) throws IncorrectFieldException;

    void deleteUserById(Long userId);
}
