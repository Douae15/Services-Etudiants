package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.AttestationScolarite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationScolariteRepository extends JpaRepository<AttestationScolarite, Integer> {
}
