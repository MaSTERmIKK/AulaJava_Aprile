package com.example.demo.Run.service;

import org.springframework.stereotype.Service;

import com.example.demo.Run.exeption.RunNotFoundException;
import com.example.demo.Run.model.Run;
import com.example.demo.Run.record.RunRequest;
import com.example.demo.Run.record.RunResponse;
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

    public List<RunResponse> findAll() {
        return runRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RunResponse findById(Integer id) {
        Run run = runRepository.findById(id)
                            .orElseThrow(() -> new RunNotFoundException(id));
        
        return toResponse(run);
    }

    public RunResponse save(RunRequest req) {
        Run run = toEntity(req);
        Run saved = runRepository.save(run);
        return toResponse(saved);
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

    private Run toEntity(RunRequest request) {
        Run run = new Run();
        run.setTitle(request.title());
        run.setStartedOn(request.startedOn());
        run.setCompletedOn(request.completedOn());
        run.setMiles(request.miles());
        run.setLocation(request.location());
        return run;
    }

    // Mapping: Run (entità JPA) → RunResponse
    private RunResponse toResponse(Run run) {
        return new RunResponse(
                run.getId(),
                run.getTitle(),
                run.getStartedOn(),
                run.getCompletedOn(),
                run.getMiles(),
                run.getLocation().name()  // converte enum in stringa
        );
    }
}
