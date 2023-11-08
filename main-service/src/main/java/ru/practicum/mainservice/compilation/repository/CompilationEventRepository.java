package ru.practicum.mainservice.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    Boolean existsByCompilationIdAndEventId(Long compId, Long eventId);
}

