package com.ensa.servicesetudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reussite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reussite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reussite")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "num_apogee", nullable = false)
    @JsonIgnore
    private Etudiant etudiant;

    @Column(name = "annee_reussite", nullable = false, length = 10)
    private String annee;

    @Column(name = "niveau_reussite", nullable = false, length = 10)
    private String niveau;

    @Column(name = "filiere_reussite", nullable = false, length = 10)
    private String filiere;

    @Column(name = "reussite")
    private Double reussite;

    @Column(name = "statut", length = 20)
    private String statut;

    @Column(name = "note_jury")
    private Double noteJury;
}
