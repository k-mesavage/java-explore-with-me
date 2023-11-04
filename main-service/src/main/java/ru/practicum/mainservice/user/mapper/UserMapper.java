package ru.practicum.mainservice.user.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.model.User;

@Service
public class UserMapper {
    public User toUser(NewUserRequest newUserRequest) {
        return User.builder().email(newUserRequest.getEmail()).name(newUserRequest.getName()).build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder().id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
