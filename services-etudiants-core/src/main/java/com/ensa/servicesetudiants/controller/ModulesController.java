package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.Modules;
import com.ensa.servicesetudiants.service.ModulesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModulesController {

    private final ModulesService modulesService;

    public ModulesController(ModulesService modulesService) {
        this.modulesService = modulesService;
    }

    @GetMapping
    public List<Modules> getAllModules() {
        return modulesService.getAllModules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Modules> getModuleById(@PathVariable Integer id) {
        return modulesService.getModuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Modules createModule(@RequestBody Modules module) {
        return modulesService.saveModule(module);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Modules> updateModule(@PathVariable Integer id, @RequestBody Modules module) {
        return modulesService.getModuleById(id)
                .map(existing -> {
                    module.setId(id);
                    return ResponseEntity.ok(modulesService.saveModule(module));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Integer id) {
        if (modulesService.getModuleById(id).isPresent()) {
            modulesService.deleteModule(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
