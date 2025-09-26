package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.ConventionStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConventionStageRepository extends JpaRepository<ConventionStage, Integer> {
}
