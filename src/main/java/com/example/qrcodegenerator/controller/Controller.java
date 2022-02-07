package com.example.qrcodegenerator.controller;

import com.example.qrcodegenerator.service.GenerationService;
import com.google.zxing.WriterException;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Tag(name = "API for QR codes generation", description = "Generates QR codes from CSV file")
@RequestMapping("/api")
public class Controller {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Controller.class);

    @Autowired
    private GenerationService generationService;

    @PostMapping(path = "/generate", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Generate QR codes from CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Generation successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    public ResponseEntity<Void> generate(@RequestBody MultipartFile file) {
        LOGGER.info("Request received with file {} and content-type {}}", file.getOriginalFilename(), file.getContentType());
        if (!isFileOK(file)) {
            return ResponseEntity.badRequest().build();
        }
        try {
            this.generationService.generate(this.generationService.parseFile(file));
        } catch (IOException | CsvValidationException | WriterException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
        LOGGER.info("QR codes generated");
        return ResponseEntity.noContent().build();
    }

    /**
     * Checks if uploaded file is OK
     *
     * @param file the csv file
     * @return true if size > 0 and content-type is text/csv, otherwise false
     */
    private boolean isFileOK(MultipartFile file) {
        if (file.getSize() == 0) {
            LOGGER.info("File is empty");
            return false;
        }
        if (!file.getContentType().equals("text/csv")) {
            LOGGER.info("File is not a CSV");
            return false;
        }
        return true;
    }
}
