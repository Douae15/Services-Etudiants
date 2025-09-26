package com.ensa.servicesetudiants.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attestation_scolarite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttestationScolarite implements Demande{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attestation_scolarite")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "num_apogee", nullable = false)
    private Etudiant etudiant;

    @Column(name = "statut", length = 20)
    private String statut;
}
