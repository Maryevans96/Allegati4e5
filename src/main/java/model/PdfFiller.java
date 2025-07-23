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
            PDPage firstPage = pdfDocument.getPage(0); // Lavoriamo sulla prima pagina

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // --- Logica per la compilazione in base al modello selezionato ---
            switch (modelName) {
                case "Enrico":
                    // Coordinate per "SCHEDA ENRICO.pdf" (basate sulle tue indicazioni precedenti)
                    addTextToPdf(contentStream, datiAllegato.getNumeroOds(), 470, 683, 10);
                    addTextToPdf(contentStream, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", 160, 508, 10);
                    addTextToPdf(contentStream, (datiAllegato.getScadenzaOds() != null) ? sdf.format(datiAllegato.getScadenzaOds()) : "", 370, 508, 10);
                    addTextToPdf(contentStream, datiAllegato.getVia(), 550, 508, 10);
                    addTextToPdf(contentStream, datiAllegato.getDanneggiante(), 120, 700, 10);
                    // Per la descrizione, se è multilinea, dovrai implementare una logica più complessa qui
                    addTextToPdf(contentStream, datiAllegato.getDescrizioneIntervento(), 140, 400, 10);
                    addTextToPdf(contentStream, (datiAllegato.getInizioLavori() != null) ? sdf.format(datiAllegato.getInizioLavori()) : "", 160, 192, 10);
                    addTextToPdf(contentStream, (datiAllegato.getFineLavori() != null) ? sdf.format(datiAllegato.getFineLavori()) : "", 170, 142, 10);
                    break;
                case "Cristoforo":
                    // *** INSERISCI QUI LE COORDINATE SPECIFICHE PER "CRISTOFORO_TEMPLATE.pdf" ***
                    // Esempio:
                    // addTextToPdf(contentStream, datiAllegato.getNumeroOds(), X_NUMERO, Y_NUMERO, FONTSIZE);
                    // addTextToPdf(contentStream, (datiAllegato.getDataOds() != null) ? sdf.format(datiAllegato.getDataOds()) : "", X_DATA, Y_DATA, FONTSIZE);
                    // ... e così via per tutti i campi
                    System.out.println("Compilazione per modello Cristoforo (coordinate da definire)");
                    break;
                case "Francesco":
                    // *** INSERISCI QUI LE COORDINATE SPECIFICHE PER "FRANCESCO_TEMPLATE.pdf" ***
                    System.out.println("Compilazione per modello Francesco (coordinate da definire)");
                    break;
                case "Congiu":
                    // *** INSERISCI QUI LE COORDINATE SPECIFICHE PER "CONGIU_TEMPLATE.pdf" ***
                    System.out.println("Compilazione per modello Congiu (coordinate da definire)");
                    break;
                default:
                    System.err.println("Modello PDF non riconosciuto o non gestito: " + modelName);
                    // Potresti voler lanciare un'eccezione o mostrare un messaggio all'utente
                    break;
            }

            contentStream.close();
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