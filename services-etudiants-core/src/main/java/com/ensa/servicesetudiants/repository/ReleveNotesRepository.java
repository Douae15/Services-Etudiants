package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.ReleveNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleveNotesRepository extends JpaRepository<ReleveNotes, Integer> {
}
