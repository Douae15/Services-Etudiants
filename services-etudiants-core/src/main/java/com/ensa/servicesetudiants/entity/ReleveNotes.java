package com.ensa.servicesetudiants.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "releve_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleveNotes implements Demande{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_releve")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "num_apogee", nullable = false)
    private Etudiant etudiant;

    @Column(name = "niveau_releve", length = 10)
    private String niveau;

    @Column(name = "statut", length = 20)
    private String statut;

    @Column(name = "filiere_releve", length = 20)
    private String filiere;
}
