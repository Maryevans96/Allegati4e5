package model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class PdfFiller {

    public void fillPdfSpecificFields(String templatePath, String outputPath, Allegati datiAllegato, String modelName) throws IOException {
        PDDocument pdfDocument = null;
        try {
            pdfDocument = PDDocument.load(new File(templatePath));

            if (pdfDocument.getNumberOfPages() < 2) {
                System.err.println("The PDF document does not have a second page for model: " + modelName);

                return;
            }

            PDPage firstPage = pdfDocument.getPage(0); // Lavoro sulla prima pagina
            PDPage secondPage = pdfDocument.getPage(1); // Lavoro sulla seconda pagina

            PDPageContentStream contentStreamFirstPage = new PDPageContentStream(pdfDocument, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
            PDPageContentStream contentStreamSecondPage = new PDPageContentStream(pdfDocument, secondPage, PDPageContentStream.AppendMode.APPEND, true, true);

            // --- GESTIONE ROTAZIONE (VERSIONE PER TESTO SOTTOSOPRA) ---
            int rotation = secondPage.getRotation();
            float width = secondPage.getMediaBox().getWidth();
            float height = secondPage.getMediaBox().getHeight();

            if (rotation == 90) {
                // Se era 90 ed è sottosopra, proviamo a traslare sull'altezza invece che sulla larghezza
                contentStreamSecondPage.transform(Matrix.getRotateInstance(Math.toRadians(90), width, 0));
            } else if (rotation == 270) {
                contentStreamSecondPage.transform(Matrix.getRotateInstance(Math.toRadians(-90), 0, height));
            } else {
                // Se anche con 0 gradi esce sottosopra, forziamo un ribaltamento
                contentStreamSecondPage.transform(Matrix.getRotateInstance(Math.toRadians(180), width, height));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // Logica per la compilazione in base al modello selezionato

            switch (modelName) {
                case "Enrico", "Congiu", "Francesco", "Cristoforo":
                    // Code for First Page
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getNumeroOds(), 482, 697, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 270, 712, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getScadenzaOds() != null) ? sdf.format(datiAllegato.getScadenzaOds()) : "", 270, 697, 10);
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getVia(), 222, 684, 10);
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getDanneggiante(), 165, 660, 10);

                    addTextToPdf(contentStreamFirstPage, datiAllegato.getDescrizioneIntervento(), 80, 555, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 200, 214, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 487, 214, 10);

                    // --- Code for Second Page ---
                    addTextToPdf(contentStreamSecondPage, datiAllegato.getNumeroOds(), 40, 335, 10);
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 110,335, 10);
                    addTextToPdf(contentStreamSecondPage, datiAllegato.getVia(), 175,335,10);
                    addTextToPdf(contentStreamSecondPage, datiAllegato.getDescrizioneIntervento(), 318,335,10);
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 490, 270, 10); // After "INIZIATE il"
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 490, 245, 10); // After "TERMINATE il"
                    break;
                default:
                    System.err.println("Unrecognized or unhandled PDF model: " + modelName);
                    break;
            }

            contentStreamFirstPage.close();
            contentStreamSecondPage.close();
            pdfDocument.save(outputPath);

        } finally {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
        }
    }

    private void addTextToPdf(PDPageContentStream contentStream, String text, float x, float y, float fontSize) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text != null ? text : "");
        contentStream.endText();
    }
}