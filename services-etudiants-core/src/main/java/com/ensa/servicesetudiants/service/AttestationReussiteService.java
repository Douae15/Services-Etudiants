package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.repository.AttestationReussiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttestationReussiteService {

    private final AttestationReussiteRepository repository;

    public AttestationReussiteService(AttestationReussiteRepository repository) {
        this.repository = repository;
    }
    
    public Optional<AttestationReussite> getById(Integer id) {
        return repository.findById(id);
    }

    public AttestationReussite save(AttestationReussite entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }


    public List<AttestationReussite> getDemandes() {
        return repository.findAll();
    }

    public AttestationReussite getDemandeById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec id " + id));
    }

    public AttestationReussite updateStatut(Integer id, String statut) {
        AttestationReussite attestation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        attestation.setStatut(statut);
        return repository.save(attestation);
    }
}
