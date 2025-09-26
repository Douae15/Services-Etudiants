package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.Etudiant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {
    Optional<Etudiant> findByEmailAndNumApogeeAndCin(String email, Integer numApogee, String cin);

    Optional<Etudiant> findByNumApogee(Integer numApogee);

}
