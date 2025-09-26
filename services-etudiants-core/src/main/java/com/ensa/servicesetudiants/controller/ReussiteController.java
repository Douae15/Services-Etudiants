package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.Reussite;
import com.ensa.servicesetudiants.service.ReussiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reussites")
public class ReussiteController {

    private final ReussiteService service;

    public ReussiteController(ReussiteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reussite> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reussite> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Reussite create(@RequestBody Reussite entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reussite> update(@PathVariable Integer id, @RequestBody Reussite entity) {
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
