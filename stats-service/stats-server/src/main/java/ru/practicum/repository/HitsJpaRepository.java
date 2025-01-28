package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Hits;

import java.time.LocalDateTime;
import java.util.Collection;

public interface HitsJpaRepository extends JpaRepository<Hits, Long> {
    Collection<Hits> findAllByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, Collection<String> uri);

    Collection<Hits> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
