package com.ensa.servicesetudiants.dto;

import java.util.List;

import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.Notes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReleveNotesDTO {
    private Etudiant etudiant;
    private List<Notes> notes;
    private String niveau;
    private String filiere;
    private double notesJury;  
    private double moyenne;   
    private String resultatAnnee;
    
}

