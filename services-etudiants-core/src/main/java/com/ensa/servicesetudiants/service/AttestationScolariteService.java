package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.repository.AttestationScolariteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttestationScolariteService {

    private final AttestationScolariteRepository repository;

    public AttestationScolariteService(AttestationScolariteRepository repository) {
        this.repository = repository;
    }

    public List<AttestationScolarite> getDemandes() {
        return repository.findAll();
    }

    public Optional<AttestationScolarite> getById(Integer id) {
        return repository.findById(id);
    }

    public AttestationScolarite getDemandeById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec id " + id));
    }

    public AttestationScolarite save(AttestationScolarite entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public AttestationScolarite updateStatut(Integer id, String statut) {
        AttestationScolarite attestation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        attestation.setStatut(statut);
        return repository.save(attestation);
    }
}
