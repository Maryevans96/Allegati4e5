package model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.*;

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


            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // Logica per la compilazione in base al modello selezionato

            switch (modelName) {
                case "Enrico", "Congiu", "Francesco", "Cristoforo":
                    // Code for First Page
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getNumeroOds(), 470, 683, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 267, 697, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getScadenzaOds() != null) ? sdf.format(datiAllegato.getScadenzaOds()) : "", 267, 683, 10);
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getVia(), 220, 670, 10);
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getDanneggiante(), 165, 645, 10);

                    addTextToPdf(contentStreamFirstPage, datiAllegato.getDescrizioneIntervento(), 195, 560, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 200, 223, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 470, 223, 10);

                    // --- Code for Second Page ---
                    addTextToPdf(contentStreamSecondPage, datiAllegato.getNumeroOds(), 60, 337, 10);
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 125,337, 10);
                    addTextToPdf(contentStreamSecondPage, datiAllegato.getVia(), 190,337,10);
                    addTextToPdf(contentStreamSecondPage, datiAllegato.getDescrizioneIntervento(), 320,337,10);
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 490, 277, 10); // After "INIZIATE il"
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 490, 252, 10); // After "TERMINATE il"
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