package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequester(Long requester);

    Optional<Request> findByIdAndRequester(Long id, Long requesterId);

    List<Request> findAllByEvent(Long event);
}
