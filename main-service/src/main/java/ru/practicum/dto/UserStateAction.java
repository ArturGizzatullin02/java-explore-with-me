package ru.practicum.dto;

import ru.practicum.model.EventState;

public enum UserStateAction {
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public EventState toEventState() {
        switch (this) {
            case SEND_TO_REVIEW:
                return EventState.WAITING;
            case CANCEL_REVIEW:
                return EventState.CANCELLED;
            default:
                throw new IllegalArgumentException("Unknown admin action: " + this);
        }
    }
}
