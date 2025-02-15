package ru.practicum.service;

import ru.practicum.GetHitsRequestParametersDto;
import ru.practicum.HitsDto;
import ru.practicum.PostHitsDto;

import java.util.Collection;

public interface HitsService {

    void saveHits(PostHitsDto postHitsDto);

    Collection<HitsDto> getHits(GetHitsRequestParametersDto parameters);
}
