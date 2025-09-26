package com.ensa.servicesetudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_note")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "num_apogee", nullable = false)
    @JsonIgnore
    private Etudiant etudiant;

    @Column(name = "nom_module", nullable = false, length = 50)
    private String nomModule;

    @Column(name = "note", nullable = false)
    private Double note;

    @Column(name = "annee_universitaire", nullable = false, length = 11)
    private String anneeUniversitaire;

    @Column(name = "filiere_note", nullable = false, length = 10)
    private String filiere;

    @Column(name = "niveau_note", length = 50)
    private String niveau;

    @Column(name = "statut_module", length = 10)
    private String statutModule;

    
}
