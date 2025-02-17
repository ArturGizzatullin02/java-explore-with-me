package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.model.EventSort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GetEventParametersUserRequest extends GetEventParametersBaseRequest {

    private String text;

    private Boolean paid;

    @Builder.Default
    private Boolean onlyAvailable = false;

    private EventSort sort;
}
