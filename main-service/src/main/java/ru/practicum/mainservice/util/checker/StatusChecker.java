package ru.practicum.mainservice.util.checker;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.IncorrectFieldException;
import ru.practicum.mainservice.util.State;

import java.util.Set;

import static ru.practicum.mainservice.util.State.CONFIRMED;
import static ru.practicum.mainservice.util.State.REJECTED;

@Service
public class StatusChecker {

    public void checkStatus(State newStatus) throws IncorrectFieldException {
        final Set<State> availableStats = Set.of(CONFIRMED, REJECTED);
        if (!availableStats.contains(newStatus)) {
            throw new IncorrectFieldException("Wrong status.");
        }
    }
}
