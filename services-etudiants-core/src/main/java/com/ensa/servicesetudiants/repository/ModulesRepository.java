package com.ensa.servicesetudiants.repository;

import com.ensa.servicesetudiants.entity.Modules;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModulesRepository extends JpaRepository<Modules, Integer> {
    List<Modules> findByAnneeModuleAndFiliereModule(String anneeModule, String filiereModule);
}
