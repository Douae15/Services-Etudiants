package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.dto.ReleveNotesDTO;
import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.Modules;
import com.ensa.servicesetudiants.entity.Notes;
import com.ensa.servicesetudiants.entity.ReleveNotes;
import com.ensa.servicesetudiants.entity.Reussite;
import com.ensa.servicesetudiants.repository.AttestationReussiteRepository;
import com.ensa.servicesetudiants.repository.EtudiantRepository;
import com.ensa.servicesetudiants.repository.ModulesRepository;
import com.ensa.servicesetudiants.repository.NotesRepository;
import com.ensa.servicesetudiants.repository.ReleveNotesRepository;
import com.ensa.servicesetudiants.repository.ReussiteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReleveNotesService {

    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private ModulesRepository moduleRepository;
    @Autowired
    private NotesRepository notesRepository;
    @Autowired
    private ReleveNotesRepository repository;
    @Autowired
    private ReussiteRepository reussiteRepository;

    public ReleveNotesDTO genererReleve(Integer numApogee, String niveau, String filiere) {
        Etudiant etudiant = etudiantRepository.findByNumApogee(numApogee)
                .orElseThrow(() -> new RuntimeException("Etudiant introuvable"));

        List<Modules> modules = moduleRepository.findByAnneeModuleAndFiliereModule(niveau, filiere);

        List<Notes> notesModules = new ArrayList<>();
        for (Modules module : modules) {
            Notes note = notesRepository
                    .findByEtudiantAndNomModuleAndNiveauAndFiliere(etudiant, module.getModule(), niveau, filiere)
                    .orElseThrow(() -> new RuntimeException("Note introuvable pour " + module.getModule()));
            notesModules.add(note);
        }

        double moyenne = getMoyenne(etudiant, filiere, niveau);
        String resultatAnnee = getResultatAnnee(etudiant, niveau, filiere);

        double notesJury = 0.0;

        return new ReleveNotesDTO(etudiant, notesModules, niveau, filiere, notesJury, moyenne, resultatAnnee);
    }

    public String getResultatAnnee(Etudiant etudiant, String anneeModule, String filiere) {
        double moyenne = getMoyenne(etudiant, filiere, anneeModule);
        return moyenne >= 10 ? "Admis" : "Ajourné";
    }

    public double getMoyenne(Etudiant etudiant, String filiere, String anneeModule) {
        List<Modules> modules = moduleRepository.findByAnneeModuleAndFiliereModule(anneeModule, filiere);
        double somme = 0;

        for (Modules module : modules) {
            Notes note = notesRepository
                    .findByEtudiantAndNomModuleAndNiveauAndFiliere(etudiant, module.getModule(), anneeModule, filiere)
                    .orElseThrow(() -> new RuntimeException("Note introuvable pour " + module.getModule()));
            somme += note.getNote();
        }
        double moyenne = somme / modules.size();
        return BigDecimal.valueOf(moyenne)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public List<ReleveNotes> getDemandes() {
        return repository.findAll();
    }

    public Optional<ReleveNotes> getById(Integer id) {
        return repository.findById(id);
    }

    public ReleveNotes getDemandeById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec id " + id));
    }

    public ReleveNotes save(ReleveNotes entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public ReleveNotes updateStatut(Integer id, String statut) {
        ReleveNotes attestation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        attestation.setStatut(statut);
        return repository.save(attestation);
    }
}
