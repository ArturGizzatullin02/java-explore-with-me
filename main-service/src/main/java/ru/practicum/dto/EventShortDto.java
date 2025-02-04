package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private LocalDateTime eventDate;

    private Boolean paid;

    private String title;

    private Integer confirmedRequests;

    private UserShortDto initiator;

    private Integer views;
}
