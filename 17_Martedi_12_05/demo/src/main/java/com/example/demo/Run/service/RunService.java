package com.example.demo.Run.service;

import org.springframework.stereotype.Service;

import com.example.demo.Run.model.Run;
import com.example.demo.Run.repository.RunRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RunService {

    private final RunRepository runRepository;

    // Iniezione via costruttore (raccomandata)
    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public List<Run> findAll() {
        return runRepository.findAll();
    }

    public Optional<Run> findById(Integer id) {
        return runRepository.findById(id);
    }

    public Run save(Run run) {
        return runRepository.save(run);
    }

    public void deleteById(Integer id) {
        runRepository.deleteById(id);
    }

    public Run update(Integer id, Run updatedRun) {
        Optional<Run> existing = runRepository.findById(id);
        if(existing.isEmpty()){
            return null;
        }
        
        Run run = existing.get();
        run.setTitle(updatedRun.getTitle());
        run.setStartedOn(updatedRun.getStartedOn());
        run.setCompletedOn(updatedRun.getCompletedOn());
        run.setMiles(updatedRun.getMiles());
        run.setLocation(updatedRun.getLocation());
        
        return runRepository.save(run);
    }
}
