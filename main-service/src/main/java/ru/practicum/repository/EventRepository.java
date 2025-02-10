package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("SELECT e FROM Event e JOIN FETCH e.location WHERE e.id IN :eventIds")
    List<Event> findAllWithCategoryAndLocationByIds(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT e FROM Event e JOIN FETCH e.location WHERE e.id = :eventId")
    Optional<Event> findWithCategoryAndLocationById(@Param("eventId") Long eventId);
}
