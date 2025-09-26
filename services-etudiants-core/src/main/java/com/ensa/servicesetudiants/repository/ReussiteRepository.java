package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.Reussite;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReussiteRepository extends JpaRepository<Reussite, Integer> {
    Optional<Reussite> findByEtudiantAndNiveauAndFiliere(Etudiant etudiant, String niveau, String filiere);
}

