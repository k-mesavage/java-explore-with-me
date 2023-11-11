package ru.practicum.mainservice.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(value = "select * from compilations where pinned = ?1 or ?1 is null " +
            "order by id asc offset ?2 rows fetch next ?3 rows only", nativeQuery = true)
    List<Compilation> findAllByParams(Boolean pinned, Integer from, Integer size);
}