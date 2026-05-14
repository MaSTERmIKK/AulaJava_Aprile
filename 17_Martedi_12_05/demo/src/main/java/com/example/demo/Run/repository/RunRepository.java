package com.example.demo.Run.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.Run.model.Run;
import java.util.List;
import java.util.Optional;

import com.example.demo.Run.Location;

@Repository
public interface RunRepository extends JpaRepository<Run, Integer>{
    
    List<Run> findByTitle(String title);

    List<Run> findByMilesGreaterThan(Integer miles);

    List<Run> findByLocation(Location location);

    List<Run> findByLocationAndMilesGreaterThan(Location location, Integer miles);
    
    Optional<Run> findFirstByTitle(String title);

    boolean existsByTitle(String title);

    @Query("SELECT r FROM Run r WHERE r.location = :location ORDER BY r.miles DESC")
    List<Run> findByLocationOrderByMiles(@Param("location") Location location);
} 
