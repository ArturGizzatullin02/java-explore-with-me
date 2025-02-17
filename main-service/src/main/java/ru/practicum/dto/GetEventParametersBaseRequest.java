package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.validator.StartBeforeEndGetEventRequestConstraint;

import java.time.LocalDateTime;
import java.util.List;

@Data
@StartBeforeEndGetEventRequestConstraint
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class GetEventParametersBaseRequest {

    private List<Long> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    @Builder.Default
    private Integer from = 0;

    @Builder.Default
    private Integer size = 10;
}
