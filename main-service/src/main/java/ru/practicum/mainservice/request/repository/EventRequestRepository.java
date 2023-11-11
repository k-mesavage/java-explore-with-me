package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.request.model.EventRequest;
import ru.practicum.mainservice.util.enums.State;

import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<EventRequest> findAllByRequesterId(Long userId);

    @Query("select r from EventRequest r where r.event.id = ?1 and r.status <> 'CONFIRMED'")
    List<EventRequest> findAllNotConfirmedRequestsByEventId(Long eventId);

    @Query("select r from EventRequest r left join Event e on r.event.id = e.id " +
            "where e.initiator.id = ?1 and e.id = ?2")
    List<EventRequest> findAllByInitiator(Long userId, Long eventId);

    @Query("select count(1) from EventRequest r where r.event.id = :eventId and r.status in :status")
    long getEventRequestCountByStatus(Long eventId, State status);

    List<EventRequest> findByIdIn(List<Long> ids);

}
