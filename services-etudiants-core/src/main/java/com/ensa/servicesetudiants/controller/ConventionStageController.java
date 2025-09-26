package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.service.ConventionStageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convention_stage")
public class ConventionStageController {

    private final ConventionStageService service;

    public ConventionStageController(ConventionStageService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConventionStage> getAll() {
        return service.getDemandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConventionStage> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ConventionStage create(@RequestBody ConventionStage entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConventionStage> update(@PathVariable Integer id, @RequestBody ConventionStage entity) {
        return service.getById(id)
                .map(existing -> {
                    entity.setId(id);
                    return ResponseEntity.ok(service.save(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (service.getById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
