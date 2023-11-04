package ru.practicum.mainservice.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {
    @Query("select ce.eventId from CompilationEvent ce where ce.compilationId = :compilationId")
    List<Long> getCompilationEventIds(Long compilationId);

    @Transactional
    void deleteByCompilationIdAndEventId(Long compId, Long eventId);

    Boolean existsByCompilationIdAndEventId(Long compId, Long eventId);
}

