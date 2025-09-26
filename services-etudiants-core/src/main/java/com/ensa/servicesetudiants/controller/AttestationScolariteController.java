package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.service.AttestationScolariteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/attest_scolarite")
public class AttestationScolariteController {

    private final AttestationScolariteService service;

    public AttestationScolariteController(AttestationScolariteService service) {
        this.service = service;
    }

    @GetMapping
    public List<AttestationScolarite> getAll() {
        return service.getDemandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttestationScolarite> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AttestationScolarite create(@RequestBody AttestationScolarite entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttestationScolarite> update(@PathVariable Integer id, @RequestBody AttestationScolarite entity) {
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
