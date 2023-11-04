package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<ParticipationRequest, Long> {

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    @Query("select r from ParticipationRequest r where r.event.id = ?1 and r.status <> 'CONFIRMED'")
    List<ParticipationRequest> findAllNotConfirmedRequestsByEventId(Long eventId);

    @Query("select r from ParticipationRequest r left join Event e on r.event.id = e.id " +
            "where e.initiator.id = ?1 and e.id = ?2")
    List<ParticipationRequest> findAllByInitiator(Long userId, Long eventId);
}
