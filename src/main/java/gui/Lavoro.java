package gui;

import model.Allegati;
import model.PdfFiller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lavoro extends JFrame {

    private JPanel contentPane;
    private JTextField numeroOdsField;
    private JTextField dataOdsField;
    private JTextField scadenzaOdsField;
    private JTextField viaField;
    private JTextField danneggianteField;
    private JTextField descrizioneInterventoField;
    private JTextField inizioLavoriField;
    private JTextField fineLavoriField;

    private JComboBox<String> aziendaComboBox;
    private JButton scaricaButton;

    private JButton compilaButton;
    private JButton pulisciCampiButton;

    private Map<String, String> pdfModels;
    private String lastCompiledFilePath;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public Lavoro() {
        super("Compilatore Documenti PDF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel modelSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        modelSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Seleziona Modello Documento"),
                new EmptyBorder(5, 10, 5, 10) // Margine interno: top, left, bottom, right
        ));


        initializePdfModels();
        aziendaComboBox = new JComboBox<>(pdfModels.keySet().toArray(new String[0]));

        modelSelectionPanel.add(new JLabel("Modello:"));
        modelSelectionPanel.add(aziendaComboBox);
        contentPane.add(modelSelectionPanel, BorderLayout.NORTH);

        JPanel dataInputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        dataInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dati per la Compilazione"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        numeroOdsField = new JTextField(20);
        dataOdsField = new JTextField(20);
        scadenzaOdsField = new JTextField(20);
        viaField = new JTextField(20);
        danneggianteField = new JTextField(20);
        descrizioneInterventoField = new JTextField(20);
        inizioLavoriField = new JTextField(20);
        fineLavoriField = new JTextField(20);

        dataInputPanel.add(new JLabel("Numero O.d.S.:"));
        dataInputPanel.add(numeroOdsField);
        dataInputPanel.add(new JLabel("Data O.d.S. (gg/mm/aaaa):"));
        dataInputPanel.add(dataOdsField);
        dataInputPanel.add(new JLabel("Scadenza O.d.S. (gg/mm/aaaa):"));
        dataInputPanel.add(scadenzaOdsField);
        dataInputPanel.add(new JLabel("Via:"));
        dataInputPanel.add(viaField);
        dataInputPanel.add(new JLabel("Danneggiante:"));
        dataInputPanel.add(danneggianteField);
        dataInputPanel.add(new JLabel("Descrizione Intervento:"));
        dataInputPanel.add(descrizioneInterventoField);
        dataInputPanel.add(new JLabel("Data Inizio Lavori (gg/mm/aaaa):"));
        dataInputPanel.add(inizioLavoriField);
        dataInputPanel.add(new JLabel("Data Fine Lavori (gg/mm/aaaa):"));
        dataInputPanel.add(fineLavoriField);

        contentPane.add(dataInputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        compilaButton = new JButton("Compila PDF");
        compilaButton.addActionListener(e -> compilePdf());

        scaricaButton = new JButton("Scarica PDF Compilato");
        scaricaButton.setEnabled(false);
        scaricaButton.addActionListener(e -> downloadPdf());

        pulisciCampiButton = new JButton("Pulisci Campi");
        pulisciCampiButton.addActionListener(e -> clearFields());

        JButton esciButton = new JButton("Esci");
        esciButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(compilaButton);
        buttonPanel.add(scaricaButton);
        buttonPanel.add(pulisciCampiButton);
        buttonPanel.add(esciButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPane);
        pack();
        setLocationRelativeTo(null);
    }

    private void initializePdfModels() {
        pdfModels = new LinkedHashMap<>();

        addModel("Enrico", "SCHEDA ENRICO.pdf");
        addModel("Cristoforo", "SCHEDA CRISTOFORO.pdf");
        addModel("Francesco", "SCHEDA FRANCESCO.pdf");
        addModel("Congiu", "SCHEDA CONGIU.pdf");
    }

    private void addModel(String modelName, String resourceFileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceFileName)) {
            if (is != null) {
                File tempFile = File.createTempFile("pdf_template_", ".pdf");
                tempFile.deleteOnExit();

                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                pdfModels.put(modelName, tempFile.getAbsolutePath());
                System.out.println("Modello '" + modelName + "' caricato da risorsa a file temporaneo: " + tempFile.getAbsolutePath());
            } else {
                System.err.println("Errore: Il file " + resourceFileName + " (per il modello " + modelName + ") non è stato trovato nella cartella resources.");
                JOptionPane.showMessageDialog(this, "Attenzione: Il modello PDF '" + modelName + "' (" + resourceFileName + ") non è stato trovato. Non sarà disponibile.", "Risorsa Mancante", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            System.err.println("Errore I/O durante il caricamento del modello " + modelName + " da risorsa: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Errore durante il caricamento del modello PDF '" + modelName + "': " + e.getMessage(), "Errore Caricamento", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void compilePdf() {
        try {
            String numeroOds = numeroOdsField.getText();
            Date dataOds = parseDate(dataOdsField.getText());
            Date scadenzaOds = parseDate(scadenzaOdsField.getText());
            String via = viaField.getText();
            String danneggiante = danneggianteField.getText();
            String descrizioneIntervento = descrizioneInterventoField.getText();
            Date inizioLavori = parseDate(inizioLavoriField.getText());
            Date fineLavori = parseDate(fineLavoriField.getText());

            Allegati dati = new Allegati(
                    numeroOds, dataOds, scadenzaOds, via, danneggiante, descrizioneIntervento, inizioLavori, fineLavori
            );

            String selectedModelName = (String) aziendaComboBox.getSelectedItem();
            String templatePdfPath = pdfModels.get(selectedModelName);

            System.out.println("Percorso Template PDF Selezionato: " + templatePdfPath);

            if (templatePdfPath == null || templatePdfPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nessun modello PDF selezionato o percorso non valido. Verificare i file in resources.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File tempOutputFile = File.createTempFile("compiled_pdf_", ".pdf");
            tempOutputFile.deleteOnExit();

            PdfFiller filler = new PdfFiller();
            filler.fillPdfSpecificFields(templatePdfPath, tempOutputFile.getAbsolutePath(), dati, selectedModelName);

            lastCompiledFilePath = tempOutputFile.getAbsolutePath();
            scaricaButton.setEnabled(true);

            JOptionPane.showMessageDialog(this, "PDF compilato con successo! Ora puoi scaricarlo.", "Successo", JOptionPane.INFORMATION_MESSAGE);

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Errore di formato data. Usa gg/mm/aaaa (es. 01/01/2023).", "Errore Input", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Errore durante la compilazione del PDF o gestione file: " + ex.getMessage(), "Errore I/O", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Si è verificato un errore inatteso: " + ex.getMessage(), "Errore Generale", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void downloadPdf() {
        if (lastCompiledFilePath == null || !new File(lastCompiledFilePath).exists()) {
            JOptionPane.showMessageDialog(this, "Nessun PDF compilato disponibile per il download.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva PDF Compilato");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));

        File desktopDir = new File(System.getProperty("user.home"), "Desktop");
        if (desktopDir.exists() && desktopDir.isDirectory()) {
            fileChooser.setCurrentDirectory(desktopDir);
        }

        String suggestedFileName = numeroOdsField.getText().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf";
        if (suggestedFileName.length() > 50) {
            suggestedFileName = "Documento_Compilato.pdf";
        }
        fileChooser.setSelectedFile(new File(suggestedFileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                if (fileToSave.exists()) {
                    int response = JOptionPane.showConfirmDialog(
                            this,
                            "Il file '" + fileToSave.getName() + "' esiste già. Vuoi sostituirlo?",
                            "Conferma Sovrascrittura",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (response == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(this, "Salvataggio annullato dall'utente.", "Annullato", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                Files.copy(
                        new File(lastCompiledFilePath).toPath(),
                        fileToSave.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                JOptionPane.showMessageDialog(this, "PDF salvato con successo in:\n" + fileToSave.getAbsolutePath(), "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Errore durante il salvataggio del file: " + ex.getMessage(), "Errore I/O", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return sdf.parse(dateString);
    }

    private void clearFields() {
        numeroOdsField.setText("");
        dataOdsField.setText("");
        scadenzaOdsField.setText("");
        descrizioneInterventoField.setText("");
        viaField.setText("");
        danneggianteField.setText("");
        inizioLavoriField.setText("");
        fineLavoriField.setText("");
        scaricaButton.setEnabled(false);
        lastCompiledFilePath = null;
        JOptionPane.showMessageDialog(this, "Campi puliti. Il PDF compilato precedente non è più disponibile per il download diretto.", "Campi Resettati", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Lavoro().setVisible(true);
        });
    }
}