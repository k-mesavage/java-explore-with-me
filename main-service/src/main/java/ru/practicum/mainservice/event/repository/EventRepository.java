package ru.practicum.mainservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.util.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findAllByInitiatorIdAndId(Long initiatorId, Long eventId);

    Integer countAllByCategoryId(Long categoryId);

    @Query(value = "select * from events e where e.state = 'PUBLISHED' and (lower(e.annotation) " +
            "like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%'))) and " +
            "(e.category_id in :categories) and (e.paid = :paid or :paid is null) " +
            "and (e.event_date between :rangeStart and :rangeEnd) " +
            "order by id asc offset :from rows fetch next :size rows only", nativeQuery = true)
    List<Event> findEventsByManyParamsWithCat(String text,
                                              List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Integer from,
                                              Integer size);

    @Query(value = "select * from events e where e.state = 'PUBLISHED' and (lower(e.annotation) " +
            "like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%'))) and " +
            "(e.category_id in :categories) and (e.paid = :paid or :paid is null) " +
            "and (e.event_date between :rangeStart and :rangeEnd) and (e.participant_limit > confirmed_requests) " +
            "order by id asc offset :from rows fetch next :size rows only", nativeQuery = true)
    List<Event> findEventsByManyParamsWithCatAvailable(String text,
                                                       List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd,
                                                       Integer from,
                                                       Integer size);

    @Query(value = "select * from events e where e.state = 'PUBLISHED' and (lower(e.annotation) " +
            "like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%'))) and (e.paid = :paid or :paid is null) " +
            "and (e.event_date between :rangeStart and :rangeEnd) " +
            "order by id asc offset :from rows fetch next :size rows only", nativeQuery = true)
    List<Event> findEventsByManyParamsWithoutCat(String text, Boolean paid, LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size);

    @Query(value = "select * from events e where e.state = 'PUBLISHED' and (lower(e.annotation) " +
            "like lower(concat('%',:text,'%')) or " +
            "lower(e.description) like lower(concat('%',:text,'%'))) and (e.paid = :paid or :paid is null) " +
            "and (e.event_date between :rangeStart and :rangeEnd) and (e.participant_limit > confirmed_requests)" +
            "order by id asc offset :from rows fetch next :size rows only", nativeQuery = true)
    List<Event> findEventsByManyParamsWithoutCatAvailable(String text, Boolean paid, LocalDateTime rangeStart,
                                                          LocalDateTime rangeEnd,
                                                          Integer from,
                                                          Integer size);

    @Query("select e from Event e where e.id = :eventId and e.state = 'PUBLISHED'")
    Event getByIdPublished(Long eventId);

    @Query("select e from Event e where (e.initiator.id in :users or :users is null) " +
            "and (:states is null or e.state in :states) and (e.category.id in :categories or :categories is null) " +
            "and (e.eventDate between :rangeStart and :rangeEnd)")
    List<Event> findEventsByManyParamsByAdmin(List<Long> users,
                                              List<State> states,
                                              List<Long> categories,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd);

    @Query("select e from Event e where e.id in :ids")
    List<Event> getAllByIds(List<Long> ids);
}
