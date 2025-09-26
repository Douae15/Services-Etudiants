package com.ensa.servicesetudiants.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "etudiant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {

    @Id
    @Column(name = "num_apogee")
    private Integer numApogee;

    @Column(name = "CIN", nullable = false, length = 10)
    private String cin;

    @Column(name = "CNE", nullable = false, length = 15)
    private String cne;

    @Column(name = "nom", nullable = false, length = 30)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 30)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    @Column(name = "addresse_mail", nullable = false, length = 120)
    private String email;

    @Column(name = "lieu_naissance", length = 50)
    private String lieuNaissance;

    @Column(name = "niveau_actuel", length = 10)
    private String niveauActuel;

    @Column(name = "filiere", length = 10)
    private String filiere;

    // Relations
    @OneToMany(mappedBy = "etudiant")
    private List<Notes> notes;

    @OneToMany(mappedBy = "etudiant")
    @JsonIgnore
    private List<AttestationReussite> attestationsReussite;

    @OneToMany(mappedBy = "etudiant")
    private List<AttestationScolarite> attestationsScolarite;

    @OneToMany(mappedBy = "etudiant")
    private List<ConventionStage> conventionsStage;

    @OneToMany(mappedBy = "etudiant")
    private List<ReleveNotes> relevesNotes;

    @OneToMany(mappedBy = "etudiant")
    private List<Reussite> reussites;
}
