package ru.practicum.stats.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.StatOutputDto;
import ru.practicum.stats.server.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerRepository extends JpaRepository<Stat, Long> {

    @Query("select new ru.practicum.stats.dto.StatOutputDto(stat.app, stat.uri, count(stat.ip)) " +
            "from Stat stat " +
            "where stat.timeStamp between cast(:start as date) and cast(:end as date) " +
            "and (stat.uri in :uris OR :uris = null) " +
            "group by stat.app, stat.uri " +
            "order by count(stat.ip) desc")
    List<StatOutputDto> getStats(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("uris") List<String> uris);

    @Query("select new ru.practicum.stats.dto.StatOutputDto(stat.app, stat.uri, count(distinct stat.ip)) " +
            "from Stat stat " +
            "where stat.timeStamp between cast(:start as date) and cast(:end as date) " +
            "and (stat.uri in :uris OR :uris = null) " +
            "group by stat.app, stat.uri " +
            "order by count(stat.ip) desc")
    List<StatOutputDto> getUniqueStats(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);
}
