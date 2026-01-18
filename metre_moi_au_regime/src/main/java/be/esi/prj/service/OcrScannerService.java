package be.esi.prj.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This service reads text from images using OCR technology.
 * It can scan pictures and find EAN codes (barcodes) in them.
 */
public class OcrScannerService {

    private final Tesseract tesseract;

    /**
     * Creates a new OcrScannerService and sets up the OCR system.
     * This is private because the class uses the singleton pattern.
     */
    private OcrScannerService() {
        this.tesseract = new Tesseract();
        try {
            URL dataUrl = getClass().getClassLoader().getResource("data");
            if (dataUrl == null) {
                throw new RuntimeException("Tesseract 'data' folder not found in resources. Make sure it exists in src/main/resources.");
            }
            File dataDir = new File(dataUrl.toURI());
            tesseract.setDatapath(dataDir.getAbsolutePath());
            tesseract.setLanguage("fra+eng");

        } catch (Exception e) {
            throw new RuntimeException("Critical error initializing Tesseract.", e);
        }
    }

    /**
     * Gets the single instance of this service.
     * This class uses the singleton pattern, so only one instance exists.
     * @return the OCR scanner service instance
     */
    public static OcrScannerService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Scans an image file and finds the EAN code (barcode) in it.
     * The image should contain a clear barcode that can be read.
     * @param file the image file to scan for a barcode
     * @return the EAN code found in the image
     */
    public String getEanCode(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("The provided file is invalid or does not exist.");
        }
        try {
            String textFromImage = tesseract.doOCR(file);
            return extractEanFromText(textFromImage);
        } catch (TesseractException e) {
            throw new RuntimeException("Error during OCR recognition: " + e.getMessage(), e);
        }
    }

    /**
     * The internal "holder" class is only loaded during the first call to getInstance().
     * This ensures lazy and thread-safe initialization.
     */
    private static class SingletonHelper {
        private static final OcrScannerService INSTANCE = new OcrScannerService();
    }

    /**
     * Looks for EAN codes in the text that was read from an image.
     * Searches for 8-digit or 13-digit numbers that could be EAN codes.
     * @param text the text that was extracted from the image
     * @return the first valid EAN code found in the text
     */
    private String extractEanFromText(String text) {
        Pattern pattern = Pattern.compile("\\b(\\d{13}|\\d{8})\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("No valid EAN code was found in the image.");
    }
}
