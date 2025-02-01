package ru.practicum.service;

import ru.practicum.HitsDto;
import ru.practicum.PostHitsDto;

import java.time.LocalDateTime;
import java.util.Collection;

public interface HitsService {

    void saveHits(PostHitsDto postHitsDto);

    Collection<HitsDto> getHits(LocalDateTime start, LocalDateTime end, Collection<String> uris, boolean unique);
}
