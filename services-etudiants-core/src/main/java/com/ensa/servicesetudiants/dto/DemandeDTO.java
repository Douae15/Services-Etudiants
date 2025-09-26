package com.ensa.servicesetudiants.dto;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.entity.ReleveNotes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDTO {

    private Integer id;
    private String statut;
    private String niveau;
    private String filiere;
    private String numApogee;
    private String nom;
    private String prenom;
    private String type;
    private String nomSociete;
    private String adresseSociete;
    private String sujet;
    private String dateDebut;
    private String dateFin;
    private String encadrantEnsa;
    private String typeStage;

    // Constructeur depuis AttestationReussite
    public DemandeDTO(AttestationReussite entity) {
        this.id = entity.getId();
        this.statut = entity.getStatut();
        this.niveau = entity.getNiveau();
        this.filiere = entity.getFiliere();
        this.type = "attest_reussite"; // <-- définir le type
        if (entity.getEtudiant() != null) {
            this.numApogee = entity.getEtudiant().getNumApogee().toString();
            this.nom = entity.getEtudiant().getNom();
            this.prenom = entity.getEtudiant().getPrenom();
        }
    }

    public DemandeDTO(AttestationScolarite entity) {
        this.id = entity.getId();
        this.statut = entity.getStatut();
        this.niveau = entity.getEtudiant().getNiveauActuel();
        this.filiere = entity.getEtudiant().getFiliere();
        this.type = "attest_scolarite"; 
        if (entity.getEtudiant() != null) {
            this.numApogee = entity.getEtudiant().getNumApogee().toString();
            this.nom = entity.getEtudiant().getNom();
            this.prenom = entity.getEtudiant().getPrenom();
        }
    }

    public DemandeDTO(ConventionStage entity) {
        this.id = entity.getId();
        this.statut = entity.getStatut();
        this.encadrantEnsa = entity.getEncadrantEnsa();
        this.typeStage = entity.getTypeStage();
        this.dateDebut = entity.getDateDebut().toString();
        this.dateFin = entity.getDateFin().toString();
        this.sujet = entity.getSujet();
        this.nomSociete = entity.getNomSociete();
        this.type = "convention_stage"; // <-- définir le type
        if (entity.getEtudiant() != null) {
            this.numApogee = entity.getEtudiant().getNumApogee().toString();
            this.nom = entity.getEtudiant().getNom();
            this.prenom = entity.getEtudiant().getPrenom();
        }
    }

    public DemandeDTO(ReleveNotes entity) {
        this.id = entity.getId();
        this.statut = entity.getStatut();
        this.niveau = entity.getNiveau();
        this.filiere = entity.getFiliere();
        this.type = "releve_notes"; // <-- définir le type
        if (entity.getEtudiant() != null) {
            this.numApogee = entity.getEtudiant().getNumApogee().toString();
            this.nom = entity.getEtudiant().getNom();
            this.prenom = entity.getEtudiant().getPrenom();
        }
    }
}