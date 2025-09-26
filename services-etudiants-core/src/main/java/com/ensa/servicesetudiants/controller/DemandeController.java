package com.ensa.servicesetudiants.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ensa.servicesetudiants.dto.DemandeDTO;
import com.ensa.servicesetudiants.dto.ReleveNotesDTO;
import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.entity.Demande;
import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.Notes;
import com.ensa.servicesetudiants.entity.ReleveNotes;
import com.ensa.servicesetudiants.service.AttestationReussiteService;
import com.ensa.servicesetudiants.service.AttestationScolariteService;
import com.ensa.servicesetudiants.service.ConventionStageService;
import com.ensa.servicesetudiants.service.EmailService;
import com.ensa.servicesetudiants.service.ReleveNotesService;
import com.ensa.servicesetudiants.service.pdfService.PdfDynamicService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/admin/demandes")
public class DemandeController {

    @Autowired
    private AttestationReussiteService reussiteService;
    @Autowired
    private AttestationScolariteService scolariteService;
    @Autowired
    private ConventionStageService conventionService;
    @Autowired
    private ReleveNotesService releveService;
    @Autowired
    private PdfDynamicService pdfDynamicService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/{type}")
    public ResponseEntity<List<DemandeDTO>> getDemandesByType(
            @PathVariable String type,
            @RequestParam(required = false) String statut) {

        List<DemandeDTO> demandes;

        switch (type) {
            case "attest_reussite":
                demandes = reussiteService.getDemandes().stream()
                        .map(DemandeDTO::new)
                        .toList();
                break;
            case "attest_scolarite":
                demandes = scolariteService.getDemandes().stream()
                        .map(DemandeDTO::new)
                        .toList();
                break;
            case "convention_stage":
                demandes = conventionService.getDemandes().stream()
                        .map(DemandeDTO::new)
                        .toList();
                break;
            case "releve_notes":
                demandes = releveService.getDemandes().stream()
                        .map(DemandeDTO::new)
                        .toList();
                break;
            default:
                return ResponseEntity.notFound().build();
        }

        if (statut != null) {
            demandes = demandes.stream()
                    .filter(d -> d.getStatut() != null && d.getStatut().equalsIgnoreCase(statut))
                    .toList();
        }

        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/{type}/{id}/statut")
    public ResponseEntity<Map<String, String>> updateStatut(
            @PathVariable String type,
            @PathVariable Integer id,
            @RequestParam String statut) {

        try {
            Demande demande;
            Etudiant etudiant;
            ConventionStage convention = null;
            // Récupération de la demande et de l'étudiant
            switch (type) {
                case "attest_reussite":
                    demande = reussiteService.getDemandeById(id);
                    reussiteService.updateStatut(id, statut);
                    etudiant = demande.getEtudiant();
                    break;
                case "attest_scolarite":
                    demande = scolariteService.getDemandeById(id);
                    scolariteService.updateStatut(id, statut);
                    etudiant = demande.getEtudiant();
                    break;
                case "convention_stage":
                    demande = conventionService.getDemandeById(id);
                    conventionService.updateStatut(id, statut);
                    etudiant = demande.getEtudiant();
                    break;
                case "releve_notes":
                    demande = releveService.getDemandeById(id);
                    releveService.updateStatut(id, statut);
                    etudiant = demande.getEtudiant();
                    break;
                default:
                    return ResponseEntity.notFound().build();
            }

            if ("acceptée".equalsIgnoreCase(statut)) {
                byte[] pdf;
                LocalDate today = LocalDate.now();
                String anneeUniversitaire;
                if (today.getMonthValue() >= 9) { 
                    anneeUniversitaire = today.getYear() + "-" + (today.getYear() + 1);
                } else { // janvier à août
                    anneeUniversitaire = (today.getYear() - 1) + "-" + today.getYear();
                }

                switch (type) {
                    case "attest_reussite":
                        AttestationReussite r = reussiteService.getDemandeById(id);
                        pdf = pdfDynamicService.generateAttestationReussite(r, anneeUniversitaire);
                        break;

                    case "attest_scolarite":
                        pdf = pdfDynamicService.generateAttestationScolarite(etudiant,
                                anneeUniversitaire);
                        break;
                    case "convention_stage":
                        pdf = pdfDynamicService.generateConventionStage(etudiant, convention);
                        break;
                    case "releve_notes":
                        ReleveNotes rn = releveService.getDemandeById(id);
                        String niveau = rn.getNiveau();
                        String filiere = rn.getFiliere();

                        ReleveNotesDTO dto = releveService.genererReleve(etudiant.getNumApogee(), niveau, filiere);

                        pdf = pdfDynamicService.generateReleveNotes(
                                dto.getEtudiant(),
                                dto.getNiveau(),
                                dto.getFiliere(),
                                dto.getNotes().stream().map(Notes::getNomModule).toList(),
                                dto.getNotes().stream().map(n -> String.valueOf(n.getNote())).toList(),
                                dto.getNotes().stream().map(Notes::getStatutModule).toList(),
                                0.0, // notes jury
                                releveService.getMoyenne(dto.getEtudiant(), dto.getFiliere(), dto.getNiveau()),
                                dto.getResultatAnnee());
                        break;

                    default:
                        throw new IllegalArgumentException("Type de demande inconnu");
                }

                emailService.sendAttestation(
                        etudiant.getEmail(),
                        etudiant.getNom() + " " + etudiant.getPrenom(),
                        pdf, type);
            }

            // Si refusé, envoyer juste un email d'information
            if ("refusée".equalsIgnoreCase(statut)) {
                emailService.sendRefus(
                        etudiant.getEmail(),
                        etudiant.getNom() + " " + etudiant.getPrenom(),
                        type 
                );
            }

            return ResponseEntity
                    .ok()
                    .header("Content-Type", "application/json")
                    .body(Map.of("message", "Statut mis à jour avec succès !"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .header("Content-Type", "application/json")
                    .body(Map.of("error", "Erreur : " + e.getMessage()));
        }
    }

    @GetMapping("/{type}/{id}/pdf")
    public ResponseEntity<ByteArrayResource> downloadPdf(
            @PathVariable String type,
            @PathVariable Integer id) throws Exception {

        byte[] pdfBytes = pdfDynamicService.generatePdf(type, id); 

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdfBytes));
    }

    // --- Renvoyer Email ---
    @PostMapping("/{type}/{id}/resend-email")
    public ResponseEntity<String> resendEmail(
            @PathVariable String type,
            @PathVariable Integer id) throws Exception {

        Demande demande;
        switch (type) {
            case "attest_reussite":
                demande = reussiteService.getDemandeById(id);
                break;
            case "attest_scolarite":
                demande = scolariteService.getDemandeById(id);
                break;
            case "convention_stage":
                demande = conventionService.getDemandeById(id);
                break;
            case "releve_notes":
                demande = releveService.getDemandeById(id);
                break;
            default:
                return ResponseEntity.notFound().build();
        }

        Etudiant etu = demande.getEtudiant();
        byte[] pdfBytes = pdfDynamicService.generatePdf(type, id);

        emailService.sendAttestation(
                etu.getEmail(),
                etu.getNom() + " " + etu.getPrenom(),
                pdfBytes, type);

        return ResponseEntity.ok("Email renvoyé avec succès !");
    }

}