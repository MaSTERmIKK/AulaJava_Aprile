package com.example.demo.Run.model;

import java.time.LocalDateTime;

import com.example.demo.Run.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "runs")
public class Run 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    @Column(name = "startedOn", nullable = false)
    private LocalDateTime startedOn;
    @Column(name = "completedOn", nullable = false)
    private LocalDateTime completedOn;
    @Column(name = "miles", nullable = false)
    private Integer miles;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;

    protected Run() {}

    public Run ( 
                String title, 
                LocalDateTime startedOn, 
                LocalDateTime completedOn,
                Integer miles,
                Location location)
    {
        this.title = title;
        if (miles < 0) throw new IllegalArgumentException("Le miglia non possono essere negative");
        if (completedOn.isBefore(startedOn)) throw new IllegalArgumentException("La data di fine deve essere dopo quella di inizio");
        this.startedOn = startedOn;
        this.completedOn = completedOn;
        this.miles = miles;
        this.location = location;
    }

    // Getters
    public Integer getId() {return id;}
    public String getTitle() {return title;}
    public LocalDateTime getStartedOn() {return startedOn;}
    public LocalDateTime getCompletedOn() {return completedOn;}
    public Integer getMiles() {return miles;}
    public Location getLocation() {return location;}

    // Setters
    public void setId(Integer id) {this.id = id;}
    public void setTitle(String title) {this.title = title;}
    public void setStartedOn(LocalDateTime startedOn) {this.startedOn = startedOn;}
    public void setCompletedOn(LocalDateTime completedOn) {this.completedOn = completedOn;}
    public void setMiles(Integer miles) {this.miles = miles;}
    public void setLocation(Location location) {this.location = location;}

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

