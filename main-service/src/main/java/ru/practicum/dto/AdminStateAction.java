package ru.practicum.dto;

import ru.practicum.model.EventState;

public enum AdminStateAction {
    PUBLISH_EVENT,
    REJECT_EVENT;

    public EventState toEventState() {
        switch (this) {
            case PUBLISH_EVENT:
                return EventState.PUBLISHED;
            case REJECT_EVENT:
                return EventState.CANCELED;
            default:
                throw new IllegalArgumentException("Unknown admin action: " + this);
        }
    }
}
