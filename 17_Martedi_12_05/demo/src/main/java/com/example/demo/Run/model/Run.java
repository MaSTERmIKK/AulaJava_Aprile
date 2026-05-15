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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "runs")
public class Run 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 100, message = "il titolo deve avere tra i 3 e i 100 caratteri" )
    private String title;

    @Column(name = "startedOn")
    @NotNull(message = "la data di inizio è obbligatoria")
    private LocalDateTime startedOn;

    @Column(name = "completedOn")
    @NotNull(message = "la data di fine è obbligatoria")
    private LocalDateTime completedOn;

    @Column(name = "miles")
    @Positive(message = "la distanza percorsa deve essere positiva")
    @Max(value = 200, message = "Non si può registrare oltre i 200 chilometri")
    private Integer miles;

    @Enumerated(EnumType.STRING)
    @Column(name = "location")
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

