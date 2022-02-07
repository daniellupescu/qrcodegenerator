package com.example.qrcodegenerator.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * GenerationService
 */
@Service
public class GenerationService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GenerationService.class);

    @Value("${qr-code.width}")
    private int width;

    @Value("${qr-code.height}")
    private int height;

    @Value("${qr-code.format}")
    private String format;

    @Value("${output.path}")
    private String outputPath;

    @Value("${csv.skip-first-line}")
    private boolean skipFirstLine;

    /**
     * Parses uploaded file to extract list of values. Creates a temporary file then deletes it
     *
     * @param multipartFile file to parse
     * @return List of values from file
     * @throws IOException
     * @throws CsvValidationException
     */
    public List<String> parseFile(MultipartFile multipartFile) throws IOException, CsvValidationException {
        Path pathOfTempFile = Paths.get(System.getProperty("java.io.tmpdir") + Instant.now().getEpochSecond() + multipartFile.getOriginalFilename());
        Files.copy(multipartFile.getInputStream(), pathOfTempFile);
        List<String> values = parseCSV(pathOfTempFile.toFile());
        Files.delete(pathOfTempFile);
        return values;
    }

    /**
     * Generates QR code images from a list of strings
     *
     * @param data list of strings
     * @throws IOException
     * @throws WriterException
     */
    public void generate(List<String> data) throws IOException, WriterException {
        for (String value : data) {
            BitMatrix bitMatrix = new QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, width, height);
            String outputFolder = (outputPath != null && !"".equals(outputPath)) ? outputPath : System.getProperty("java.io.tmpdir");
            StringBuilder sb = new StringBuilder();
            sb.append(outputFolder);
            sb.append(value);
            sb.append(".");
            sb.append(format);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(sb.toString()));
            MatrixToImageWriter.writeToStream(bitMatrix, format, fileOutputStream);
            fileOutputStream.close();
        }
    }

    /**
     * Parses a CSV file with the CSVReader lib
     * @param file to parse
     * @return list of values
     * @throws IOException
     * @throws CsvValidationException
     */
    private List<String> parseCSV(File file) throws IOException, CsvValidationException {
        List<String> list = new LinkedList<>();
        String[] line;
        try (CSVReader reader = new CSVReader(new FileReader(file))){
            if (skipFirstLine) {
                reader.skip(1);
            }
            while ((line = reader.readNext()) != null) {
                list.add(line[0]);
            }
        }
        LOGGER.info("CSV parsing completed");
        return list;
    }
}