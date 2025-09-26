package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.Etudiant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationReussiteRepository extends JpaRepository<AttestationReussite, Integer> {
}
