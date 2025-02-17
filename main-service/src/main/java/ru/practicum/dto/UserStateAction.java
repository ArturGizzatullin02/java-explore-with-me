package ru.practicum.dto;

import ru.practicum.model.EventState;

public enum UserStateAction {
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public EventState toEventState() {
        switch (this) {
            case SEND_TO_REVIEW:
                return EventState.PENDING;
            case CANCEL_REVIEW:
                return EventState.CANCELED;
            default:
                throw new IllegalArgumentException("Unknown admin action: " + this);
        }
    }
}
