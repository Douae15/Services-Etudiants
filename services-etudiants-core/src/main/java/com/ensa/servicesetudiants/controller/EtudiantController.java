package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.ReleveNotes;
import com.ensa.servicesetudiants.repository.AttestationReussiteRepository;
import com.ensa.servicesetudiants.repository.AttestationScolariteRepository;
import com.ensa.servicesetudiants.repository.ConventionStageRepository;
import com.ensa.servicesetudiants.repository.EtudiantRepository;
import com.ensa.servicesetudiants.repository.ReleveNotesRepository;
import com.ensa.servicesetudiants.service.EtudiantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;

    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private AttestationScolariteRepository attestationScolariteRepository;
    @Autowired
    private ReleveNotesRepository releveNotesRepository;
    @Autowired
    private ConventionStageRepository conventionStageRepository;
    @Autowired
    private AttestationReussiteRepository attestationReussiteRepository;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getAllEtudiants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Integer id) {
        return etudiantService.getEtudiantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Etudiant createEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.saveEtudiant(etudiant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(@PathVariable Integer id, @RequestBody Etudiant etudiant) {
        return etudiantService.getEtudiantById(id)
                .map(existing -> {
                    etudiant.setNumApogee(id);
                    return ResponseEntity.ok(etudiantService.saveEtudiant(etudiant));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Integer id) {
        if (etudiantService.getEtudiantById(id).isPresent()) {
            etudiantService.deleteEtudiant(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/valider")
    public Map<String, Boolean> validerEtudiant(@RequestBody Etudiant etudiant) {
        System.out.println("📩 Reçu : " + etudiant); // debug
        boolean existe = etudiantRepository
                .findByEmailAndNumApogeeAndCin(
                        etudiant.getEmail(),
                        etudiant.getNumApogee(),
                        etudiant.getCin())
                .isPresent();
        return Map.of("valide", existe);
    }

    @PostMapping("/demandes/{type}")
    public ResponseEntity<Void> creerDemande(
            @PathVariable String type,
            @RequestBody Map<String, Object> payload) {
        try {
            Object numObj = payload.get("numApogee");
            Integer numApogee;

            if (numObj instanceof Number) {
                numApogee = ((Number) numObj).intValue();
            } else if (numObj instanceof String) {
                numApogee = Integer.parseInt((String) numObj);
            } else {
                throw new IllegalArgumentException("numApogee invalide: " + numObj);
            }

            Etudiant etu = etudiantRepository.findByNumApogee(numApogee)
                    .orElseThrow(() -> new IllegalArgumentException("Étudiant introuvable"));

            switch (type) {
                case "scolarite":
                    AttestationScolarite att = new AttestationScolarite();
                    att.setEtudiant(etu);
                    att.setStatut("non traitée");
                    attestationScolariteRepository.save(att);
                    break;

                case "releve":
                    ReleveNotes rel = new ReleveNotes();
                    rel.setEtudiant(etu);
                    rel.setNiveau((String) payload.get("niveau"));
                    rel.setFiliere((String) payload.get("filiere"));
                    rel.setStatut("non traitée");
                    releveNotesRepository.save(rel);
                    break;

                case "stage":
                    ConventionStage stage = new ConventionStage();
                    stage.setEtudiant(etu);
                    stage.setTypeStage((String) payload.get("type_stage"));
                    stage.setSujet((String) payload.get("sujet"));
                    stage.setEncadrantEnsa((String) payload.get("encadrant_ensa"));
                    stage.setEncadrantEntreprise((String) payload.get("encadrant_entreprise"));
                    stage.setNomSociete((String) payload.get("nom_societe"));
                    stage.setTeleSociete((String) payload.get("tele_societe"));
                    stage.setMailSociete((String) payload.get("mail_societe"));
                    stage.setAdresseSociete((String) payload.get("adresse_societe"));
                    stage.setRepresentantSociete((String) payload.get("representant_societe"));
                    stage.setPositionRepresentant((String) payload.get("position_representant"));

                    // Conversion des dates
                    if (payload.get("date_debut") != null) {
                        LocalDate dateDebutLocal = LocalDate.parse((String) payload.get("date_debut"));
                        stage.setDateDebut(Date.from(dateDebutLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                    if (payload.get("date_fin") != null) {
                        LocalDate dateFinLocal = LocalDate.parse((String) payload.get("date_fin"));
                        stage.setDateFin(Date.from(dateFinLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                    stage.setStatut("non traitée");

                    conventionStageRepository.save(stage);
                    break;

                case "reussite":
                    AttestationReussite reussite = new AttestationReussite();
                    reussite.setEtudiant(etu);
                    reussite.setNiveau((String) payload.get("niveau"));
                    reussite.setFiliere((String) payload.get("filiere"));
                    reussite.setStatut("non traitée");
                    attestationReussiteRepository.save(reussite);
                    break;

                default:
                    return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}
