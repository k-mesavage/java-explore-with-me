package ru.practicum.mainservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;
    private final UserRepository repository;

    @Override
    public List<User> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from/size, size);
        return repository.findByIdIn(ids, pageRequest).stream().collect(Collectors.toList());
    }

//TODO*
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = mapper.toUser(newUserRequest);
        try {
            return mapper.toDto(repository.save(user));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = String.format("User with email='%s' already exists!", newUserRequest.getEmail());
            log.warn(errorMessage);
            throw new RuntimeException();
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        if(!repository.existsById(userId)) {
            log.warn("User with id={} was not found!", userId);
            throw new ObjectNotFoundException("User", "Not found");
        }
        repository.deleteById(userId);
    }
}
