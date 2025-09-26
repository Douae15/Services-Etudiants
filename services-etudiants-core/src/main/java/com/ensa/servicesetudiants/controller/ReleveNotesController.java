package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.ReleveNotes;
import com.ensa.servicesetudiants.service.ReleveNotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/releve_notes")
public class ReleveNotesController {

    private final ReleveNotesService service;

    public ReleveNotesController(ReleveNotesService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReleveNotes> getAll() {
        return service.getDemandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReleveNotes> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ReleveNotes create(@RequestBody ReleveNotes entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReleveNotes> update(@PathVariable Integer id, @RequestBody ReleveNotes entity) {
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
