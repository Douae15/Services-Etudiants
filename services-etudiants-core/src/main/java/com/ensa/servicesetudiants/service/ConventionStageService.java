package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.repository.ConventionStageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConventionStageService {

    private final ConventionStageRepository repository;

    public ConventionStageService(ConventionStageRepository repository) {
        this.repository = repository;
    }

    public List<ConventionStage> getDemandes() {
        return repository.findAll();
    }

    public Optional<ConventionStage> getById(Integer id) {
        return repository.findById(id);
    }

    public ConventionStage getDemandeById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec id " + id));
    }

    public ConventionStage save(ConventionStage entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public ConventionStage updateStatut(Integer id, String statut) {
        ConventionStage attestation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        attestation.setStatut(statut);
        return repository.save(attestation);
    }
}
