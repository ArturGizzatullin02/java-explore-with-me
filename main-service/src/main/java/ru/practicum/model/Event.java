package ru.practicum.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {

    private Long id;

    private String annotation;

    private Category category;

    private String description;

    private LocalDateTime eventDate;

    // TODO location

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String title;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private User initiator; // TODO здесь только name(в дто)

    private LocalDateTime publishedOn;

    private EventState state;

    private Integer views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
