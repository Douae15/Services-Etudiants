package com.ensa.servicesetudiants.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "convention_stage")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ConventionStage implements Demande{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convention")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "num_apogee", nullable = false)
    private Etudiant etudiant;

    @Column(name = "type_stage", nullable = false, length = 50)
    private String typeStage;

    @Column(name = "encadrant_ensa", nullable = false, length = 200)
    private String encadrantEnsa;

    @Column(name = "encadrant_entreprise", nullable = false, length = 200)
    private String encadrantEntreprise;

    @Column(name = "date_debut", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateDebut;

    @Column(name = "date_fin", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateFin;

    @Column(name = "sujet", length = 50)
    private String sujet;

    @Column(name = "nom_societe", length = 100)
    private String nomSociete;

    @Column(name = "adresse_societe", length = 100)
    private String adresseSociete;

    @Column(name = "tele_societe", length = 100)
    private String teleSociete;

    @Column(name = "mail_societe", length = 100)
    private String mailSociete;

    @Column(name = "representant_societe", length = 100)
    private String representantSociete;

    @Column(name = "position_representant", length = 100)
    private String positionRepresentant;

    @Column(name = "statut", length = 20)
    private String statut;
}