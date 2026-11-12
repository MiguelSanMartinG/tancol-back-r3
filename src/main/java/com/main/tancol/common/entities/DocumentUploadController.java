package com.main.tancol.common.entities;

import com.main.tancol.common.repository.DocumentRepository;
import com.main.tancol.common.repository.UploadedImageRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@Tag(name = "📄 Document")
public class DocumentUploadController {
    private static final String UPLOAD_DIR = "documents/";
    private final DocumentRepository documentRepository;

    public DocumentUploadController(DocumentRepository documentRepository) throws IOException {
        this.documentRepository = documentRepository;
        Files.createDirectories(storagePath);
    }

    private final Path storagePath = Paths.get("uploads");

    @PostMapping("/upload-document")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF");
        }

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = storagePath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = filename;
        return ResponseEntity.ok(fileUrl);
    }



    @GetMapping("/documents/{filename:.+}")
    public ResponseEntity<Resource> viewPdf(@PathVariable String filename) throws IOException {
        Path file = storagePath.resolve(filename);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
