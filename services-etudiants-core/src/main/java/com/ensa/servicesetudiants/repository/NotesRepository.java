package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.Notes;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Integer> {

    Optional<Notes> findByEtudiantAndNomModuleAndNiveauAndFiliere(
            Etudiant etudiant,
            String nomModule,
            String niveau,
            String filiere);

    List<Notes> findByEtudiantAndNiveauAndFiliere(Etudiant etudiant, String niveau, String filiere);
}
