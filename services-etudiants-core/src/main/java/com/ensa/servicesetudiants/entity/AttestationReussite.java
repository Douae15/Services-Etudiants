package com.ensa.servicesetudiants.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attestation_reussite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttestationReussite implements Demande{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attestation_reussite")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "num_apogee", nullable = false)
    private Etudiant etudiant;

    @Column(name = "statut", nullable = false, length = 11)
    private String statut;

    @Column(name = "niveau_attestation", length = 50)
    private String niveau;

    @Column(name = "filiere_attestation", length = 10)
    private String filiere;
}
