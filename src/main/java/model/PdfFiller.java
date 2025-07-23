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

            // Ensure the document has at least two pages if you intend to fill the second one
            if (pdfDocument.getNumberOfPages() < 2) {
                System.err.println("The PDF document does not have a second page for model: " + modelName);
                // You might want to throw an exception or handle this case differently
                return;
            }

            PDPage firstPage = pdfDocument.getPage(0); // Lavoriamo sulla prima pagina
            PDPage secondPage = pdfDocument.getPage(1); // Lavoriamo sulla seconda pagina

            PDPageContentStream contentStreamFirstPage = new PDPageContentStream(pdfDocument, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
            PDPageContentStream contentStreamSecondPage = new PDPageContentStream(pdfDocument, secondPage, PDPageContentStream.AppendMode.APPEND, true, true);


            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // --- Logica per la compilazione in base al modello selezionato ---
            switch (modelName) {
                case "Enrico":
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getNumeroOds(), 470, 683, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 267, 697, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getScadenzaOds() != null) ? sdf.format(datiAllegato.getScadenzaOds()) : "", 267, 683, 10);
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getVia(), 220, 670, 10);
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getDanneggiante(), 165, 645, 10);
                    // For description, if it's multi-line, you'll need more complex logic here
                    addTextToPdf(contentStreamFirstPage, datiAllegato.getDescrizioneIntervento(), 195, 560, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 200, 223, 10);
                    addTextToPdf(contentStreamFirstPage, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 470, 223, 10);



                    addTextToPdf(contentStreamSecondPage, datiAllegato.getNumeroOds(), 60, 337, 10); // Adjusted for a smaller font if inside table

                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 125,337, 10);

                    addTextToPdf(contentStreamSecondPage, datiAllegato.getVia(), 190,337,10);

                    addTextToPdf(contentStreamSecondPage, datiAllegato.getDescrizioneIntervento(), 320,337,10);

                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 490, 277, 10); // After "INIZIATE il"
                    addTextToPdf(contentStreamSecondPage, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 490, 252, 10); // After "TERMINATE il"

                    break;
                case "Cristoforo":
                    // *** INSERT SPECIFIC COORDINATES FOR "CRISTOFORO_TEMPLATE.pdf" ***
                    System.out.println("Compilation for Cristoforo model (coordinates to be defined)");
                    break;
                case "Francesco":
                    // *** INSERT SPECIFIC COORDINATES FOR "FRANCESCO_TEMPLATE.pdf" ***
                    System.out.println("Compilation for Francesco model (coordinates to be defined)");
                    break;
                case "Congiu":
                    // *** INSERT SPECIFIC COORDINATES FOR "CONGIU_TEMPLATE.pdf" ***
                    System.out.println("Compilation for Congiu model (coordinates to be defined)");
                    break;
                default:
                    System.err.println("Unrecognized or unhandled PDF model: " + modelName);
                    break;
            }

            contentStreamFirstPage.close();
            contentStreamSecondPage.close(); // Close the second page's content stream
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