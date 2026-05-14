package com.example.demo.Run.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Run.model.Run;
import com.example.demo.Run.repository.RunRepository;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/runs")
public class RunController {
    
    private final RunRepository runRepository;

    public RunController(RunRepository runRepository){
        this.runRepository = runRepository;
    }

    @GetMapping
    public List<Run> findAll(){
        return runRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Run> findById(@PathVariable Integer id) {
        Optional<Run> run = runRepository.findById(id);
        return run.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Run> create(@RequestBody Run run){
        Run saved = runRepository.save(run);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Run> update(@PathVariable Integer id, @RequestBody Run runDetails) {
        Optional<Run> existing = runRepository.findById(id);
        if(existing.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Run run = existing.get();
        run.setTitle(runDetails.getTitle());
        run.setStartedOn(runDetails.getStartedOn());
        run.setCompletedOn(runDetails.getCompletedOn());
        run.setMiles(runDetails.getMiles());
        run.setLocation(runDetails.getLocation());
        Run update = runRepository.save(run);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        if(!runRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        runRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
