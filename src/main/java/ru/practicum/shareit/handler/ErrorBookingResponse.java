package ru.practicum.shareit.handler;

import lombok.Data;

@Data
public class ErrorBookingResponse {
    private final String error;

    public ErrorBookingResponse(String description) {
        this.error = "Unknown state: " + description;
    }
}
