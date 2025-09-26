package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.Modules;
import com.ensa.servicesetudiants.repository.ModulesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModulesService {

    private final ModulesRepository modulesRepository;

    public ModulesService(ModulesRepository modulesRepository) {
        this.modulesRepository = modulesRepository;
    }

    public List<Modules> getAllModules() {
        return modulesRepository.findAll();
    }

    public Optional<Modules> getModuleById(Integer id) {
        return modulesRepository.findById(id);
    }

    public Modules saveModule(Modules module) {
        return modulesRepository.save(module);
    }

    public void deleteModule(Integer id) {
        modulesRepository.deleteById(id);
    }
}
