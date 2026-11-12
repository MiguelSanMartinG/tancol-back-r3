package com.main.tancol.common.entities;

import com.main.tancol.common.repository.UploadedImageRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@Tag(name = "\uD83D\uDDBC\uFE0F Thumbnail")
public class ImageUploadController {
    private static final String UPLOAD_DIR = "uploads/";
    private final UploadedImageRepository uploadedImageRepository;

    public ImageUploadController(UploadedImageRepository uploadedImageRepository) {
        this.uploadedImageRepository = uploadedImageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map> uploadImage(@RequestParam("file") MultipartFile file) {
        try {

            log.info("Uploading image to " + file);
            // Check if file is empty
            if (file == null || file.isEmpty()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "No file uploaded or file is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
            // Validar el nombre del archivo
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre del archivo no es válido.");
                throw new RuntimeException("\"El nombre del archivo no es válido.\"");
            }

            // Crear el directorio si no existe
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                log.info("Creating new directory: {}", UPLOAD_DIR);
                uploadDir.mkdir();
            }

            // Obtener la extensión del archivo
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // Generar un nombre único para el archivo
            UUID id = UUID.randomUUID();
            String randomFileName = id.toString() + fileExtension;

            // Crear y guardar la entidad UploadedImage
            UploadedImage uploadedImage = UploadedImage.builder()
                    .id(id)
                    .extension(fileExtension)
                    .build();
            uploadedImageRepository.save(uploadedImage);

            // Guardar el archivo en el directorio
            Path path = Paths.get(UPLOAD_DIR + randomFileName);
            log.info("Uploaded image {}", path);
            Files.write(path, file.getBytes());

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("fileName", randomFileName);

            return ResponseEntity.status(HttpStatus.OK).body(respuesta);

        } catch (ObjectOptimisticLockingFailureException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(respuesta);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", e.getMessage());
            log.error("Error al subir imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Cambia el tipo de contenido según el tipo de imagen
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
