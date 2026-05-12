package com.example.demo.Run;

import java.time.LocalDateTime;

public class Run 
{
    private Integer id;
    private String title;
    private LocalDateTime startedOn;
    private LocalDateTime completedOn;
    private Integer miles;
    private Location location;

    public Run (Integer id, 
                String title, 
                LocalDateTime startedOn, 
                LocalDateTime completedOn,
                Integer miles,
                Location location){

                    this.id = id;
                    this.title = title;
                    this.startedOn = startedOn;
                    this.completedOn = completedOn;
                    this.miles = miles;
                    this.location = location;
                }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public LocalDateTime getStartedOn() {
        return startedOn;
    }

    public LocalDateTime getCompletedOn() {
        return completedOn;
    }

    public Integer getMiles() {
        return miles;
    }

    public Location getLocation() {
        return location;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartedOn(LocalDateTime startedOn) {
        this.startedOn = startedOn;
    }
    public void setCompletedOn(LocalDateTime completedOn) {
        this.completedOn = completedOn;
    }

    public void setMiles(Integer miles) {
        this.miles = miles;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    // toString method
    @Override
    public String toString() {
        return "Run{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", startedOn=" + startedOn +
        ", completedOn=" + completedOn +
        ", miles=" + miles +
        ", location=" + location +
        '}';
    }

}

