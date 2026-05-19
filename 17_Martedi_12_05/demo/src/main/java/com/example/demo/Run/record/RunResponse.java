package com.example.demo.Run.record;

import java.time.LocalDateTime;

public record RunResponse(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    int miles,
    String location
) {}
