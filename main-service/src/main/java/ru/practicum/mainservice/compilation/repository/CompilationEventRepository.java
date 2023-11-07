package ru.practicum.mainservice.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    List<Long> getCompilationByEventIdIn(List<Long> compilationId);

    void deleteByCompilationIdAndEventId(Long compId, Long eventId);

    Boolean existsByCompilationIdAndEventId(Long compId, Long eventId);
}

