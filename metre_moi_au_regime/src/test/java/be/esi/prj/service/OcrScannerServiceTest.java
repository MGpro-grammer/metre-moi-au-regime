package be.esi.prj.service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class OcrScannerServiceTest {

    @Test
    void testOcrScannerService() {
        File imageFile;
        String path = "src/main/resources/dataForUnitTests/test-barcode.jpg";
        imageFile = new File(path);
        OcrScannerService ocrScannerService = OcrScannerService.getInstance();

        try {
            String rawText = ocrScannerService.getEanCode(imageFile);
            System.out.println("Texte détecté par OCR : " + rawText);
        } catch (Exception e) {
            System.out.println("Erreur OCR : " + e.getMessage());
        }

        String eanCode = ocrScannerService.getEanCode(imageFile);
        System.out.println("EAN Code: " + eanCode);
    }
}