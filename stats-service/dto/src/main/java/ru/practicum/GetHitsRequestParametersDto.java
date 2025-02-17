package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.validator.StartBeforeEndHitRequestConstraint;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@StartBeforeEndHitRequestConstraint
public class GetHitsRequestParametersDto {

    @NotNull(message = "Стартовое время поиска не может быть null")
    private LocalDateTime start;

    @NotNull(message = "Конечное время поиска не может быть null")
    private LocalDateTime end;

    private Collection<String> uris;

    private boolean unique;
}
