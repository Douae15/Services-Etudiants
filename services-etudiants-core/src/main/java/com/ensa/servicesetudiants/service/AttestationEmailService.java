package com.ensa.servicesetudiants.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ensa.servicesetudiants.service.pdfService.PdfDynamicService;

@Service
public class AttestationEmailService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfDynamicService pdfService;

    @Autowired
    private AttestationReussiteService reussiteService;
    @Autowired
    private AttestationScolariteService scolariteService;
    @Autowired
    private ConventionStageService conventionService;
    @Autowired
    private ReleveNotesService releveService;

    public void envoyerAttestation(String typeDemande, Integer id) throws Exception {
        // Génération du PDF selon le type
        byte[] pdfBytes = pdfService.generatePdf(typeDemande, id);

        String email, nomPrenom;

        // Récupération de l'étudiant et mise à jour du statut
        switch (typeDemande) {
            case "attest_reussite":
                var r = reussiteService.getDemandeById(id);
                email = r.getEtudiant().getEmail();
                nomPrenom = r.getEtudiant().getNom() + " " + r.getEtudiant().getPrenom();
                r.setStatut("acceptée");
                reussiteService.save(r);
                break;
            case "attest_scolarite":
                var s = scolariteService.getDemandeById(id);
                email = s.getEtudiant().getEmail();
                nomPrenom = s.getEtudiant().getNom() + " " + s.getEtudiant().getPrenom();
                s.setStatut("acceptée");
                scolariteService.save(s);
                break;
            case "convention_stage":
                var c = conventionService.getDemandeById(id);
                email = c.getEtudiant().getEmail();
                nomPrenom = c.getEtudiant().getNom() + " " + c.getEtudiant().getPrenom();
                c.setStatut("acceptée");
                conventionService.save(c);
                break;
            case "releve_notes":
                var rn = releveService.getDemandeById(id);
                email = rn.getEtudiant().getEmail();
                nomPrenom = rn.getEtudiant().getNom() + " " + rn.getEtudiant().getPrenom();
                rn.setStatut("acceptée");
                releveService.save(rn);
                break;
            default:
                throw new IllegalArgumentException("Type de demande inconnu: " + typeDemande);
        }

        // Envoi de l'email avec le PDF et le corps dynamique selon le type
        emailService.sendAttestation(email, nomPrenom, pdfBytes, typeDemande);
    }

    public void envoyerRefus(String typeDemande, Integer id) throws Exception {
        String email, nomPrenom;

        switch (typeDemande) {
            case "attest_reussite":
                var r = reussiteService.getDemandeById(id);
                email = r.getEtudiant().getEmail();
                nomPrenom = r.getEtudiant().getNom() + " " + r.getEtudiant().getPrenom();
                r.setStatut("refusée");
                reussiteService.save(r);
                break;
            case "attest_scolarite":
                var s = scolariteService.getDemandeById(id);
                email = s.getEtudiant().getEmail();
                nomPrenom = s.getEtudiant().getNom() + " " + s.getEtudiant().getPrenom();
                s.setStatut("refusée");
                scolariteService.save(s);
                break;
            case "convention_stage":
                var c = conventionService.getDemandeById(id);
                email = c.getEtudiant().getEmail();
                nomPrenom = c.getEtudiant().getNom() + " " + c.getEtudiant().getPrenom();
                c.setStatut("refusée");
                conventionService.save(c);
                break;
            case "releve_notes":
                var rn = releveService.getDemandeById(id);
                email = rn.getEtudiant().getEmail();
                nomPrenom = rn.getEtudiant().getNom() + " " + rn.getEtudiant().getPrenom();
                rn.setStatut("refusée");
                releveService.save(rn);
                break;
            default:
                throw new IllegalArgumentException("Type de demande inconnu: " + typeDemande);
        }

        emailService.sendRefus(email, nomPrenom, typeDemande);
    }
}
