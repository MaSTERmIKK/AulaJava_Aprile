package com.example.demo.Run;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record RunRecord(
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    Integer miles,
    Location location
) 
{
    public RunRecord{
        if(miles < 0){
            miles = 0;
            // throw new IllegalArgumentException("Miles cannot be negative: " + miles);
        }
        if(completedOn.isBefore(startedOn)){
            completedOn = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
            // throw new IllegalArgumentException("completedOn must be after startedOn");
        }
    }

}
