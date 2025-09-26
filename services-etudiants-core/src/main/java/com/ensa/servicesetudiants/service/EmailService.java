package com.ensa.servicesetudiants.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAttestation(String toEmail, String nomPrenom, byte[] pdfBytes, String typeDemande) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("admij4685@gmail.com", "ENSA Administration");
        helper.setTo(toEmail);

        String sujet = "";
        String corps = "";
        String fichier = "";

        switch (typeDemande) {
            case "attest_reussite":
                sujet = "Attestation de réussite";
                corps = "Bonjour " + nomPrenom + ",\n\nVeuillez trouver ci-joint votre attestation de réussite.";
                fichier = "attestation_reussite.pdf";
                break;
            case "attest_scolarite":
                sujet = "Attestation de scolarité";
                corps = "Bonjour " + nomPrenom + ",\n\nVeuillez trouver ci-joint votre attestation de scolarité.";
                fichier = "attestation_scolarite.pdf";
                break;
            case "convention_stage":
                sujet = "Convention de stage";
                corps = "Bonjour " + nomPrenom + ",\n\nVeuillez trouver ci-joint votre convention de stage.";
                fichier = "convention_stage.pdf";
                break;
            case "releve_notes":
                sujet = "Relevé de notes";
                corps = "Bonjour " + nomPrenom + ",\n\nVeuillez trouver ci-joint votre relevé de notes.";
                fichier = "releve_notes.pdf";
                break;
            default:
                throw new IllegalArgumentException("Type de demande inconnu: " + typeDemande);
        }

        helper.setSubject(sujet);
        helper.setText(corps);
        helper.addAttachment(fichier, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    public void sendRefus(String toEmail, String nomPrenom, String typeDemande) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("admij4685@gmail.com", "ENSA Administration");
        helper.setTo(toEmail);

        String sujet = "Refus de votre demande";
        String corps = "Bonjour " + nomPrenom + ",\n\nVotre demande concernant ";

        switch (typeDemande) {
            case "attest_reussite":
                corps += "l'attestation de réussite a été refusée.";
                break;
            case "attest_scolarite":
                corps += "l'attestation de scolarité a été refusée.";
                break;
            case "convention_stage":
                corps += "la convention de stage a été refusée.";
                break;
            case "releve_notes":
                corps += "le relevé de notes a été refusé.";
                break;
        }

        helper.setSubject(sujet);
        helper.setText(corps);

        mailSender.send(message);
    }
}
