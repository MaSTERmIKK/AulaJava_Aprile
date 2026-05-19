package com.example.demo.Run.record;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp
) {

}
