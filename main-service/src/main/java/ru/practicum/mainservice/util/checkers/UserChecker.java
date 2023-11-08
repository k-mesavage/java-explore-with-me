package ru.practicum.mainservice.util.checkers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.IncorrectObjectException;
import ru.practicum.mainservice.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserChecker {

    private final UserRepository userRepository;
    public void checkUserExists(Long userId) throws IncorrectObjectException {
        if (!userRepository.existsById(userId)) {
            throw new IncorrectObjectException("There is no user with id = " + userId);
        }
    }
}
