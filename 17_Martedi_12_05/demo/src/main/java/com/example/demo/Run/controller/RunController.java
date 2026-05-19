package com.example.demo.Run.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Run.model.Run;
import com.example.demo.Run.record.RunRequest;
import com.example.demo.Run.record.RunResponse;
import com.example.demo.Run.service.RunService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/runs")
public class RunController {
    
    private final RunService runService;

    public RunController(RunService runService){
        this.runService = runService;
    }

    @GetMapping
    public List<RunResponse> findAll(){
        return runService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RunResponse> findById(@PathVariable Integer id) {
        RunResponse run = runService.findById(id);
        return ResponseEntity.ok(run);
    }
    
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RunRequest run, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            System.out.println(errors);
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(runService.save(run));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Run> update(@PathVariable Integer id, @Valid @RequestBody Run runDetails) {
        Run updated = runService.update(id, runDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        if(runService.findById(id) == null){
            return ResponseEntity.notFound().build();
        }

        runService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
