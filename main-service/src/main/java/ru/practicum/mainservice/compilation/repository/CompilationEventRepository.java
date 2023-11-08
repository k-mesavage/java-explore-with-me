package ru.practicum.mainservice.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.compilation.model.CompilationEvent;


public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {
}

