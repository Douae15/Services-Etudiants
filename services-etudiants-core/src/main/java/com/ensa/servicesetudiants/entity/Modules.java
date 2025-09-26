package com.ensa.servicesetudiants.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Modules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_module")
    private Integer id;

    @Column(name = "module", nullable = false, length = 50)
    private String module;

    @Column(name = "annee_module", nullable = false, length = 10)
    private String anneeModule;

    @Column(name = "filiere_module", nullable = false, length = 10)
    private String filiereModule;
}
