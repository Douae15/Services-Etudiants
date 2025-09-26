package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.Reussite;
import com.ensa.servicesetudiants.repository.ReussiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReussiteService {

    private final ReussiteRepository repository;

    public ReussiteService(ReussiteRepository repository) {
        this.repository = repository;
    }

    public List<Reussite> getAll() {
        return repository.findAll();
    }

    public Optional<Reussite> getById(Integer id) {
        return repository.findById(id);
    }

    public Reussite save(Reussite entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
