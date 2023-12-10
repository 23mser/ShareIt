package ru.practicum.shareit.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String error;
    private String description;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
