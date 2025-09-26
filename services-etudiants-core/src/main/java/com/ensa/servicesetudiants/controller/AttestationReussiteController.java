package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.service.AttestationReussiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/attest_reussite")
public class AttestationReussiteController {

    private final AttestationReussiteService service;

    public AttestationReussiteController(AttestationReussiteService service) {
        this.service = service;
    }

    @GetMapping
    public List<AttestationReussite> getAll() {
        return service.getDemandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttestationReussite> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AttestationReussite create(@RequestBody AttestationReussite entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttestationReussite> update(@PathVariable Integer id, @RequestBody AttestationReussite entity) {
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

    @PutMapping("/{id}/statut")
    public AttestationReussite updateStatut(@PathVariable Integer id, @RequestParam String statut) {
        return service.updateStatut(id, statut);
    }
}
