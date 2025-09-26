package com.ensa.servicesetudiants.service.pdfService;

import com.ensa.servicesetudiants.dto.ReleveNotesDTO;
import com.ensa.servicesetudiants.entity.AttestationReussite;
import com.ensa.servicesetudiants.entity.AttestationScolarite;
import com.ensa.servicesetudiants.entity.ConventionStage;
import com.ensa.servicesetudiants.entity.Etudiant;
import com.ensa.servicesetudiants.entity.Notes;
import com.ensa.servicesetudiants.entity.ReleveNotes;
import com.ensa.servicesetudiants.service.AttestationReussiteService;
import com.ensa.servicesetudiants.service.AttestationScolariteService;
import com.ensa.servicesetudiants.service.ConventionStageService;
import com.ensa.servicesetudiants.service.ReleveNotesService;
import java.util.List;
import java.util.ArrayList;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

@Service
public class PdfDynamicService {

        @Autowired
        private AttestationReussiteService reussiteService;
        @Autowired
        private AttestationScolariteService scolariteService;
        @Autowired
        private ConventionStageService conventionService;
        @Autowired
        private ReleveNotesService releveService;

        public byte[] generatePdf(String type, Integer id) throws Exception {
                switch (type) {
                        case "attest_reussite":
                                AttestationReussite r = reussiteService.getDemandeById(id);
                                return generateAttestationReussite(r, computeAnneeUniversitaire());

                        case "attest_scolarite":
                                AttestationScolarite s = scolariteService.getDemandeById(id);
                                return generateAttestationScolarite(s.getEtudiant(), computeAnneeUniversitaire());

                        case "convention_stage":
                                ConventionStage c = conventionService.getDemandeById(id);
                                return generateConventionStage(c.getEtudiant(), c);

                        case "releve_notes":
                                ReleveNotes rn = releveService.getDemandeById(id);

                                ReleveNotesDTO dto = releveService.genererReleve(
                                                rn.getEtudiant().getNumApogee(),
                                                rn.getNiveau(),
                                                rn.getFiliere());

                                return generateReleveNotes(
                                                dto.getEtudiant(),
                                                dto.getNiveau(),
                                                dto.getFiliere(),
                                                dto.getNotes().stream().map(Notes::getNomModule).toList(),
                                                dto.getNotes().stream().map(n -> String.valueOf(n.getNote())).toList(),
                                                dto.getNotes().stream().map(Notes::getStatutModule).toList(),
                                                dto.getNotesJury(), dto.getMoyenne(),
                                                dto.getResultatAnnee());

                        default:
                                throw new IllegalArgumentException("Type de demande inconnu: " + type);
                }
        }

        private String computeAnneeUniversitaire() {
                LocalDate today = LocalDate.now();
                if (today.getMonthValue() >= 9) {
                        return today.getYear() + "-" + (today.getYear() + 1);
                } else {
                        return (today.getYear() - 1) + "-" + today.getYear();
                }
        }

        // conversion mm -> points (1 mm ≈ 2.83464567 points)
        private static float mmToPt(float mm) {
                return mm * 2.83464567f;
        }

        private static String formatDateFr(Date date) {
                if (date == null)
                        return "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
                return sdf.format(date);
        }

        public byte[] generateAttestationReussite(AttestationReussite attestation, String anneeUniversitaire) throws Exception {
                Etudiant etudiant = attestation.getEtudiant();
                Document document = new Document(PageSize.A5);
                document.setMargins(0, 0, 0, 0);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();

                PdfContentByte cb = writer.getDirectContent();
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();

                // --- Logo
                Image logo = Image.getInstance(getClass().getResource("/pdf/university.png"));
                float logoWpt = mmToPt(100f);
                float logoHpt = mmToPt(15f);
                logo.scaleAbsolute(logoWpt, logoHpt);
                float logoX = mmToPt(22f);
                float logoY = pageHeight - mmToPt(1f) - logoHpt;
                logo.setAbsolutePosition(logoX, logoY);
                document.add(logo);

                // --- Titre encadré
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                float boxW = mmToPt(100f);
                float boxH = mmToPt(8f);
                float boxX = (pageWidth - boxW) / 2f;
                float boxY = logoY - mmToPt(12f);
                cb.rectangle(boxX, boxY, boxW, boxH);
                cb.stroke();
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("ATTESTATION DE REUSSITE", titleFont),
                                boxX + boxW / 2f, boxY + (boxH / 2f) - 3f, 0);

                float cursorY = boxY - mmToPt(15f);

                Font normal = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
                Font bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("Le Directeur de l'Ecole Nationale des sciences Appliquees de Tetouan atteste que",
                                                normal),
                                pageWidth / 2f, cursorY, 0);
                cursorY -= mmToPt(8f);

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("l'etudiant(e) " + (etudiant.getNom() == null ? ""
                                                : etudiant.getNom().toUpperCase() +
                                                                " "
                                                                + (etudiant.getPrenom() == null ? ""
                                                                                : etudiant.getPrenom())),
                                                bold),
                                pageWidth / 2f, cursorY, 0);
                cursorY -= mmToPt(7f);

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("nee le " + formatDateFr(etudiant.getDateNaissance()) + " a " +
                                                (etudiant.getLieuNaissance() == null ? ""
                                                                : etudiant.getLieuNaissance()),
                                                normal),
                                pageWidth / 2f, cursorY, 0);
                cursorY -= mmToPt(7f);

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("a ete declaree admise au niveau", normal),
                                pageWidth / 2f, cursorY, 0);
                cursorY -= mmToPt(7f);

                // Supposons que tu as déjà récupéré la demande par son ID
                String filiere = attestation.getFiliere() == null ? "" : attestation.getFiliere();
                String niveau = attestation.getNiveau() == null ? "" : attestation.getNiveau();
                String filiereText;
                if ("2ap".equalsIgnoreCase(filiere)) {
                        filiereText = niveau + " preparatoire";
                } else {
                        filiereText = niveau + " de cycle ingenieur: " + filiere;
                }
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase(filiereText, bold),
                                pageWidth / 2f, cursorY, 0);
                cursorY -= mmToPt(7f);

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("au titre de l'annee universitaire "
                                                + (anneeUniversitaire == null ? "" : anneeUniversitaire), normal),
                                pageWidth / 2f, cursorY, 0);

                float lineY = cursorY - mmToPt(3f);
                cb.setLineWidth(0.5f);
                cb.moveTo(pageWidth / 2f - mmToPt(30f), lineY);
                cb.lineTo(pageWidth / 2f + mmToPt(30f), lineY);
                cb.stroke();

                float blockBottom = cursorY - mmToPt(65f);

                // --- Date
                LocalDate today = LocalDate.now();
                String mois = today.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
                String dateText = today.getDayOfMonth() + " " + mois + " " + today.getYear();

                float sigY = mmToPt(25f); // Signature proche du bas (25 mm depuis le bas par exemple)
                float sigX = mmToPt(80f);

                ColumnText.showTextAligned(
                                cb,
                                Element.ALIGN_RIGHT,
                                new Phrase("Fait à Tétouan le " + dateText, new Font(Font.FontFamily.HELVETICA, 8)),
                                pageWidth - mmToPt(20f),
                                sigY + mmToPt(30f), // placé 15 mm au-dessus de la signature
                                0);

                // --- Signature
                Image signature = Image.getInstance(getClass().getResource("/pdf/signature.jpg"));
                signature.scaleAbsolute(mmToPt(60f), mmToPt(20f));
                signature.setAbsolutePosition(sigX, sigY - mmToPt(5f));
                document.add(signature);

                // --- N etudiant
                ColumnText.showTextAligned(
                                cb,
                                Element.ALIGN_LEFT,
                                new Phrase(
                                                "N étudiant : " + (etudiant.getNumApogee() == null ? ""
                                                                : etudiant.getNumApogee().toString()),
                                                new Font(Font.FontFamily.HELVETICA, 8)),
                                mmToPt(5f), // <-- réduit de 20f à 10f pour rapprocher du bord gauche
                                mmToPt(18f),
                                0);

                // --- Avis important
                ColumnText.showTextAligned(
                                cb,
                                Element.ALIGN_CENTER, // <-- centré
                                new Phrase(
                                                "Avis important: Il ne peut être délivré qu'un seul exemplaire de cette attestation. Aucun duplicata ne sera fourni",
                                                new Font(Font.FontFamily.HELVETICA, 7)),
                                mmToPt(64f),
                                mmToPt(10f), // placé juste en bas
                                0);

                document.close();

                // DEBUG optionnel: écrire fichier sur disque pour vérifier l'apparence
                // Files.write(Path.of("C:/temp/test_attestation.pdf"), out.toByteArray());

                return out.toByteArray();
        }

        public byte[] generateAttestationScolarite(Etudiant etudiant, String anneeUniversitaire) throws Exception {
                Document document = new Document(PageSize.A5);
                document.setMargins(0, 0, 0, 0);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();

                PdfContentByte cb = writer.getDirectContent();
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();

                // --- En-tête
                Image header = Image.getInstance(getClass().getResource("/pdf/en_tete_attestation_scolarite.png"));
                header.scaleAbsolute(mmToPt(120f), mmToPt(35f));
                header.setAbsolutePosition(mmToPt(10f), pageHeight - mmToPt(36f));
                document.add(header);

                Font normal = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
                Font bold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

                float cursorY = pageHeight - mmToPt(48f);

                // --- Texte principal identique au PHP (avec espaces)
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("            Le directeur de l'Ecole Nationale des Sciences Appliquee de Tetouan atteste que",
                                                normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(6f);

                Phrase phrase = new Phrase();
                phrase.add(new Chunk("            L'étudiant(e) :  ", normal));
                phrase.add(new Chunk(etudiant.getNom().toUpperCase() + " " + etudiant.getPrenom(), bold));

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, phrase, mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(10f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("            Numero de la carte d'identite nationale :    "
                                                + etudiant.getCin(), normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(10f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("            Code nationale de l'etudiant(e) :                "
                                                + etudiant.getCne(), normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(10f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("            Nee le " + formatDateFr(etudiant.getDateNaissance()) + " a "
                                                + etudiant.getLieuNaissance(), normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(8f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("            Poursuit ses etudes a l'Ecole Nationale des Sciences Appliquee de Tetouan pour l'annee",
                                                normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(4f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("            universitaires " + anneeUniversitaire, normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(8f);

                // --- Filière et niveau
                String filiere = etudiant.getFiliere() == null ? "" : etudiant.getFiliere();
                String niveau = etudiant.getNiveauActuel() == null ? "" : etudiant.getNiveauActuel();

                if (!"2ap".equalsIgnoreCase(filiere)) {
                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                        new Phrase("            Diplome :      Diplome d'ingenieur filiere " + filiere,
                                                        normal),
                                        mmToPt(10f), cursorY, 0);
                        cursorY -= mmToPt(8f);
                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                        new Phrase("            Filiere :      Ingenieur " + filiere, normal),
                                        mmToPt(10f), cursorY, 0);
                        cursorY -= mmToPt(8f);
                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                        new Phrase("           Annee :" + niveau + " du Cycle Ingenieur  " + filiere
                                                        + ".", normal),
                                        mmToPt(10f), cursorY, 0);
                        cursorY -= mmToPt(10f);
                } else {
                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                        new Phrase("            Diplome :      Diplome d'ingenieur .", normal),
                                        mmToPt(10f), cursorY, 0);
                        cursorY -= mmToPt(8f);
                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                        new Phrase("            Filiere :      Ingenieur en " + filiere + ".", normal),
                                        mmToPt(10f), cursorY, 0);
                        cursorY -= mmToPt(8f);
                        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                        new Phrase("            Annee :        " + niveau + " du Cycle Preparatoire .",
                                                        normal),
                                        mmToPt(10f), cursorY, 0);
                        cursorY -= mmToPt(10f);
                }

                // --- Date
                LocalDate today = LocalDate.now();
                String mois = today.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
                String dateText = today.getDayOfMonth() + " " + mois + " " + today.getYear();

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("           Fait a Tetouan le " + dateText, normal),
                                pageWidth / 2, mmToPt(80f), 0);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("              Le Directeur", normal),
                                pageWidth / 2, mmToPt(73f), 0);

                // --- Signature
                Image signature = Image.getInstance(getClass().getResource("/pdf/signature.jpg"));
                signature.scaleAbsolute(mmToPt(60f), mmToPt(20f));
                signature.setAbsolutePosition(mmToPt(88f), mmToPt(53f));
                document.add(signature);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("                                       N etudiant : "
                                                + etudiant.getNumApogee(), normal),
                                pageWidth / 2, mmToPt(50f), 0);

                // --- Pied
                Image footer = Image.getInstance(getClass().getResource("/pdf/fin_attestation_scolarite.png"));
                footer.scaleAbsolute(mmToPt(140f), mmToPt(20f));
                footer.setAbsolutePosition(mmToPt(5f), mmToPt(20f));
                document.add(footer);

                document.close();
                return out.toByteArray();
        }

        public byte[] generateConventionStage(Etudiant etudiant, ConventionStage convention) throws Exception {
                Document document = new Document(PageSize.A5);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();

                PdfContentByte cb = writer.getDirectContent();
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();

                Font normal = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
                Font bold = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);

                // ================== PAGE 1 ==================
                document.newPage();

                Image header = Image.getInstance(getClass().getResource("/pdf/debut_stage.png"));
                header.setAbsolutePosition(mmToPt(10f), pageHeight - mmToPt(60f));
                header.scaleAbsolute(mmToPt(125f), mmToPt(60f));
                document.add(header);

                float cursorY = pageHeight - mmToPt(65f);

                // Société
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("La Societe : ", normal), mmToPt(10f),
                                cursorY, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(convention.getNomSociete() + ".", bold),
                                mmToPt(25f), cursorY, 0);
                cursorY -= mmToPt(5f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Adresse : ", normal), mmToPt(10f),
                                cursorY, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase(convention.getAdresseSociete() + ".", bold), mmToPt(23f), cursorY, 0);
                cursorY -= mmToPt(5f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Tel : ", normal), mmToPt(10f), cursorY,
                                0);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(convention.getTeleSociete() + ".", bold),
                                mmToPt(15f), cursorY, 0);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Email : ", normal), mmToPt(60f), cursorY,
                                0);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(convention.getMailSociete() + ".", bold),
                                mmToPt(70f), cursorY, 0);
                cursorY -= mmToPt(5f);

                // Représentant + en qualite (avec retour à la ligne)
                Phrase representantPhrase = new Phrase();
                representantPhrase.add(new Chunk("Representee par Monsieur ", normal));
                representantPhrase.add(new Chunk(convention.getRepresentantSociete(), bold));
                representantPhrase.add(new Chunk("\nen qualite ", normal));
                representantPhrase.add(new Chunk(convention.getPositionRepresentant(), bold));

                ColumnText ct = new ColumnText(cb);
                ct.setSimpleColumn(mmToPt(10f), cursorY - mmToPt(15f), mmToPt(130f), cursorY);
                ct.addText(representantPhrase);
                ct.go();
                cursorY -= mmToPt(15f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Ci-apres denommee ", normal),
                                mmToPt(80f), cursorY, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("L'ENTREPRISE.", bold), mmToPt(110f),
                                cursorY, 0);
                cursorY -= mmToPt(10f);

                // ================== Article 1 ==================
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("Article 1 : Engagement", bold),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(1f);

                String nomPrenom = etudiant.getNom().toUpperCase() + " " + etudiant.getPrenom();
                String filiere = etudiant.getFiliere();
                String niveau = etudiant.getNiveauActuel();

                // Texte Article 1
                Phrase article1 = new Phrase();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateDebutStr = sdf.format(convention.getDateDebut());

                article1.add(new Chunk("L'ENTREPRISE accepte de recevoir à titre de stagiaire ", normal));
                article1.add(new Chunk(nomPrenom, bold));
                if (!"2ap".equalsIgnoreCase(filiere)) {
                        article1.add(new Chunk(" étudiant de la filière du Cycle Ingénieur ", normal)); // normal
                        article1.add(new Chunk(filiere + " " + niveau, bold));
                } else {
                        article1.add(new Chunk(" étudiant de ", normal));
                        article1.add(new Chunk(niveau + " Préparatoire", bold));
                }
                article1.add(new Chunk(
                                " de l'ENSA de Tétouan, Université Abdelmalek Essaadi (Tétouan), "
                                                + "pour une période allant du ",
                                normal));
                article1.add(new Chunk(dateDebutStr, bold));
                article1.add(new Chunk(" au ", normal));
                article1.add(new Chunk(convention.getDateFin() + ".", bold));
                article1.add(new Chunk(
                                "\nEn aucun cas, cette convention ne pourra autoriser les étudiants \n"
                                                + "à s'absenter durant la période des contrôles ou des enseignements.",
                                bold));

                // Mise en colonne
                ct = new ColumnText(cb);
                ct.setSimpleColumn(mmToPt(10f), cursorY - mmToPt(35f), mmToPt(135f), cursorY);
                ct.setLeading(12f); // interligne (à ajuster si besoin)
                ct.addText(article1);
                ct.go();

                cursorY -= mmToPt(28f); // descendre le curseur après le bloc

                // ================== Article 2 ==================
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("Article 2 : Objet", bold),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(1f); // espace sous le titre

                Phrase article2 = new Phrase();
                article2.add(new Chunk(
                                "Le stage aura pour objet essentiel d'assurer l'application pratique de l'enseignement donné par ",
                                normal));
                article2.add(new Chunk("l'Établissement", bold));
                article2.add(new Chunk(
                                ", et ce, en organisant des visites sur les installations et en réalisant des études proposées par ",
                                normal));
                article2.add(new Chunk("L'ENTREPRISE.", bold));

                // Mise en colonne
                ct = new ColumnText(cb);
                ct.setSimpleColumn(mmToPt(10f), cursorY - mmToPt(25f), mmToPt(135f), cursorY);
                ct.setLeading(12f);
                ct.addText(article2);
                ct.go();

                cursorY -= mmToPt(10f);

                // Article 3
                Phrase article3 = new Phrase();
                article3.add(new Chunk("Article 3 : Encadrement et suivi\n", bold));
                article3.add(new Chunk(
                                "Pour accompagner le Stagiaire durant son stage, et ainsi instaurer une veritable collaboration\n",
                                normal));

                article3.add(new Chunk(
                                "L'ENTREPRISE/Stagiaire/Etablissement, L'ENTREPRISE designe Mme/Mr ",
                                normal));
                article3.add(new Chunk(convention.getEncadrantEntreprise(), bold));
                article3.add(new Chunk(
                                " encadrant(e) et parrain(e), pour superviser et assurer la qualite du travail fourni par le Stagiaire.\n",
                                normal));
                article3.add(new Chunk(
                                "L'Établissement désigne ", normal));
                article3.add(new Chunk(convention.getEncadrantEnsa(), bold));
                article3.add(new Chunk(" en tant que tuteur qui procurera une assistance pedagogique.", normal));

                // Placement du texte
                ct = new ColumnText(cb);
                ct.setLeading(12f);
                ct.setSimpleColumn(
                                mmToPt(10f),
                                cursorY - mmToPt(35f),
                                mmToPt(135f),
                                cursorY);
                ct.addText(article3);
                ct.go();

                // Ajustement du curseur
                cursorY -= mmToPt(25f);

                // Article 4
                Phrase article4 = new Phrase();
                article4.add(new Chunk("Article 4 : Programme\n", bold));
                article4.add(new Chunk("Le thème du stage est : ", normal));
                article4.add(new Chunk(convention.getSujet() + ".\n", bold));
                article4.add(new Chunk(
                                "Ce programme a été défini conjointement par ",
                                normal));
                article4.add(new Chunk(
                                "l'Établissement, l'ENTREPRISE et le Stagiaire.\n", bold));
                article4.add(new Chunk(
                                "Le contenu de ce programme doit permettre au Stagiaire une réflexion en relation avec les enseignements ",
                                normal));
                article4.add(new Chunk("ou le projet de fin d'études ", normal));
                article4.add(new Chunk(
                                "qui s'inscrit dans le programme de formation de l'", normal));
                article4.add(new Chunk("Établissement.", bold));

                // Placement du texte
                ct = new ColumnText(cb);
                ct.setLeading(12f);
                ct.setSimpleColumn(
                                mmToPt(10f),
                                cursorY - mmToPt(40f),
                                mmToPt(135f),
                                cursorY);
                ct.addText(article4);
                ct.go();

                // Ajustement du curseur
                cursorY -= mmToPt(30f);

                // ================== PAGE 2 ==================
                document.newPage();

                Image articlesImg = Image.getInstance(getClass().getResource("/pdf/articles_stage.png"));
                articlesImg.setAbsolutePosition(mmToPt(8f), pageHeight - mmToPt(140f));
                articlesImg.scaleAbsolute(mmToPt(130f), mmToPt(130f));
                document.add(articlesImg);

                cursorY = pageHeight - mmToPt(150f);

                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now(); // récupère l'heure actuelle
                String mois = today.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);

                // Formatte la date
                String dateText = today.getDayOfMonth() + " " + mois + " " + today.getYear();

                // Formatte l'heure en HH:mm:ss
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String timeText = now.format(timeFormatter);

                // Texte final avec heure
                String fullDateText = dateText + " à " + timeText;

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("                                                             Fait à Tétouan en quatre exemplaires, le "
                                                + fullDateText, normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(7f);

                float pageCenterX = pageWidth / 2;

                // Titre des signatures
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("Nom et signature du Stagiaire                                  Le Coordonnateur de la filiere",
                                                normal),
                                pageCenterX, cursorY, 0);
                cursorY -= mmToPt(5f);

                // Nom du stagiaire, juste en dessous
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase(nomPrenom, bold),
                                mmToPt(35f), cursorY, 0);
                cursorY -= mmToPt(15f);

                // Signatures des établissements
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                                new Phrase("Signature et cache de L'Etablissement                          Signature et cache de L'ENTREPRISE",
                                                normal),
                                pageCenterX, cursorY, 0);

                document.close();
                return out.toByteArray();
        }

        public byte[] generateReleveNotes(Etudiant etudiant, String niveau, String filiere,
                        List<String> modules, List<String> notes, List<String> statutModules,
                        Double notesJury, double moyenne, String resultatAnnee) throws Exception {

                Document document = new Document(PageSize.A5);
                document.setMargins(0, 0, 0, 0);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();

                PdfContentByte cb = writer.getDirectContent();
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();

                // --- Logo en-tête
                Image logo = Image.getInstance(getClass().getResource("/pdf/entete_releve.png"));
                logo.scaleAbsolute(mmToPt(135f), mmToPt(35f));
                logo.setAbsolutePosition(mmToPt(8f), pageHeight - mmToPt(36f));
                document.add(logo);

                // Polices
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                Font normal = new Font(bf, 9, Font.NORMAL);
                Font bold = new Font(bf, 9, Font.BOLD);

                float cursorY = pageHeight - mmToPt(45f);

                // --- Infos étudiant
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("    L'élève Ingénieur :  ", normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(5f);

                Phrase nomPhrase = new Phrase();
                nomPhrase.add(new Chunk("    Nom et Prénom   :  ", normal));
                nomPhrase.add(new Chunk(etudiant.getNom().toUpperCase() + " " + etudiant.getPrenom(), bold));
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, nomPhrase, mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(5f);

                Phrase cnePhrase = new Phrase();
                cnePhrase.add(new Chunk("    CNE                     : ", normal));
                cnePhrase.add(new Chunk(etudiant.getCne(), bold));
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, cnePhrase, mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(5f);

                Phrase apogeePhrase = new Phrase();
                apogeePhrase.add(new Chunk("    Code Apogée       : ", normal));
                apogeePhrase.add(new Chunk(String.valueOf(etudiant.getNumApogee()), bold));
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, apogeePhrase, mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(5f);

                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("    A obtenu les résultats suivants pour la " + niveau +
                                                " de la filière " + filiere + " : ", normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(10f);

                // --- Table des modules (centrée)
                PdfPTable tableModules = new PdfPTable(3);
                tableModules.setWidths(new float[] { 35, 35, 35 });
                tableModules.setTotalWidth(mmToPt(105f));
                tableModules.setLockedWidth(true);

                PdfPCell cell;

                // Header
                cell = new PdfPCell(new Phrase("Intitulé du Module", bold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidth(1);
                tableModules.addCell(cell);

                cell = new PdfPCell(new Phrase("Note/20", bold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidth(1);
                tableModules.addCell(cell);

                cell = new PdfPCell(new Phrase("Résultat", bold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderWidth(1);
                tableModules.addCell(cell);

                // Modules
                for (int i = 0; i < modules.size(); i++) {
                        cell = new PdfPCell(new Phrase(modules.get(i), bold));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableModules.addCell(cell);

                        cell = new PdfPCell(new Phrase(notes.get(i), normal));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tableModules.addCell(cell);

                        cell = new PdfPCell(new Phrase(statutModules.get(i), normal));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tableModules.addCell(cell);
                }

                // Points du jury
                cell = new PdfPCell(new Phrase("Points du jury", normal));
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableModules.addCell(cell);

                String juryText = (notesJury == null || notesJury == 0.0) ? "" : String.valueOf(notesJury);
                cell = new PdfPCell(new Phrase(juryText, normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableModules.addCell(cell);

                // Centrer le tableau des modules
                float tableModulesX = (pageWidth - tableModules.getTotalWidth()) / 2;
                tableModules.writeSelectedRows(0, -1, tableModulesX, cursorY, cb);
                cursorY -= mmToPt(70f);

                // --- Table Moyenne et Résultat (mini-tableau à droite)
                PdfPTable tableResultat = new PdfPTable(2);
                tableResultat.setWidths(new float[] { 35, 35 });
                tableResultat.setTotalWidth(mmToPt(70f));
                tableResultat.setLockedWidth(true);

                // Moyenne
                cell = new PdfPCell(new Phrase("Moyenne de l'année", bold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableResultat.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(moyenne), normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableResultat.addCell(cell);

                // Résultat
                cell = new PdfPCell(new Phrase("Résultat de l'année", bold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableResultat.addCell(cell);

                cell = new PdfPCell(new Phrase(resultatAnnee, normal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableResultat.addCell(cell);

                // Position à droite
                float tableResultatX = pageWidth - mmToPt(20f) - tableResultat.getTotalWidth();
                tableResultat.writeSelectedRows(0, -1, tableResultatX, cursorY, cb);
                cursorY -= mmToPt(20f);

                // --- Texte final
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("La présente attestation est délivrée à l'intéressé(e) pour servir et valoir ce que de droit.",
                                                normal),
                                mmToPt(10f), cursorY, 0);
                cursorY -= mmToPt(10f);

                LocalDate today = LocalDate.now();
                String mois = today.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
                String dateText = today.getDayOfMonth() + " " + mois + " " + today.getYear();
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                                new Phrase("Fait à Tétouan le : " + dateText, normal),
                                mmToPt(10f), cursorY, 0);

                // --- Footer
                Image footer = Image.getInstance(getClass().getResource("/pdf/fin_releve.png"));
                footer.scaleAbsolute(mmToPt(135f), mmToPt(13f));
                footer.setAbsolutePosition(mmToPt(10f), mmToPt(5f));
                document.add(footer);

                document.close();
                return out.toByteArray();
        }

}
